import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class SistemaPedidos {
    public static void main(String[] args) throws Exception {
        BufferedReader lector = new BufferedReader(
                new InputStreamReader(System.in));

        String catalogo = lector.readLine();
        String operaciones = lector.readLine();

        ProductoRepository repositorio = new ProductoRepositoryImpl();
        PedidoService servicio = new PedidoServiceImpl(repositorio);
        Pedido pedido = Pedido.empty();

        if (catalogo != null
                && !catalogo.trim().isEmpty()
                && !catalogo.trim().equals("-")) {

            for (String registro : catalogo.split(";")) {
                String[] datos = registro.split("\\|", 3);

                repositorio.guardar(new ProductoDTO(
                        datos[0].trim(),
                        datos[1].trim(),
                        datos[2].trim()
                ));
            }
        }

        StringBuilder salida = new StringBuilder();

        if (operaciones != null
                && !operaciones.trim().isEmpty()
                && !operaciones.trim().equals("-")) {

            for (String operacion : operaciones.split(";")) {
                String[] datos = operacion.split("\\|");
                String accion = datos[0].trim();

                if (accion.equals("AGREGAR")) {
                    int id = Integer.parseInt(datos[1].trim());
                    int cantidad = Integer.parseInt(datos[2].trim());

                    EstadoAgregarProducto estado =
                            servicio.agregarProducto(
                                    pedido, id, cantidad);

                    salida.append("AGREGAR ")
                            .append(id)
                            .append(" ")
                            .append(estado)
                            .append("\n");

                } else if (accion.equals("CONFIRMAR")) {
                    TipoDescuento tipo =
                            TipoDescuento.valueOf(datos[1].trim());

                    ResumenPedidoDTO resumen =
                            servicio.confirmarPedido(pedido, tipo);

                    for (DetallePedidoDTO detalle
                            : resumen.getDetalles()) {

                        salida.append("ITEM ")
                                .append(detalle.getIdProducto())
                                .append(" ")
                                .append(detalle.getNombre())
                                .append(" ")
                                .append(detalle.getCantidad())
                                .append("\n");
                    }

                    salida.append(String.format(
                            Locale.US,
                            "RESUMEN %.2f %.2f %.2f",
                            resumen.getSubtotal(),
                            resumen.getDescuento(),
                            resumen.getTotal()
                    ));
                }
            }
        }

        System.out.println(salida);
    }
}

enum TipoDescuento {
    REGULAR, FRECUENTE, MAYOREO
}

enum EstadoAgregarProducto {
    AGREGADO, PRODUCTO_NO_EXISTE, CANTIDAD_INVALIDA
}

final class ProductoDTO {
    private final String id;
    private final String nombre;
    private final String precio;

    public ProductoDTO(String id, String nombre, String precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPrecio() { return precio; }
}

final class DetallePedidoDTO {
    private final int idProducto;
    private final String nombre;
    private final int cantidad;

    public DetallePedidoDTO(
            int idProducto, String nombre, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
}

final class ResumenPedidoDTO {
    private final List<DetallePedidoDTO> detalles;
    private final double subtotal;
    private final double descuento;
    private final double total;

    public ResumenPedidoDTO(
            List<DetallePedidoDTO> detalles,
            double subtotal,
            double descuento,
            double total) {

        this.detalles = Collections.unmodifiableList(
                new ArrayList<DetallePedidoDTO>(detalles));
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
    }

    public List<DetallePedidoDTO> getDetalles() { return detalles; }
    public double getSubtotal() { return subtotal; }
    public double getDescuento() { return descuento; }
    public double getTotal() { return total; }
}

interface DescuentoStrategy {
    double calcularDescuento(double subtotal);
}

interface Discountable {
    void setDiscount(DescuentoStrategy descuentoStrategy);
}

interface ProductoRepository {
    boolean guardar(ProductoDTO producto);
    Producto buscarPorId(int idProducto);
    boolean existe(int idProducto);
}

interface PedidoService {
    EstadoAgregarProducto agregarProducto(
            Pedido pedido, int idProducto, int cantidad);

    DescuentoStrategy SelectorDescuento(
            TipoDescuento tipoDescuento);

    ResumenPedidoDTO confirmarPedido(
            Pedido pedido, TipoDescuento tipoDescuento);
}

final class Producto {
    private final int id;
    private final String nombre;
    private final double precio;

    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
}

final class ElementoPedido {
    private final Producto producto;
    private int cantidad;

    public ElementoPedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }

    public void agregarCantidad(int adicional) {
        cantidad += adicional;
    }

    public double calcularSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}

final class Pedido implements Discountable {
    private final String id;
    private final LocalDateTime fechaCreacion;
    private final Map<Integer, ElementoPedido> elementos;
    private DescuentoStrategy descuento;

    private Pedido() {
        id = UUID.randomUUID().toString();
        fechaCreacion = LocalDateTime.now();
        elementos = new LinkedHashMap<Integer, ElementoPedido>();
        descuento = new DescuentoRegular();
    }

    public static Pedido empty() {
        return new Pedido();
    }

    public String getId() { return id; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    @Override
    public void setDiscount(DescuentoStrategy estrategia) {
        descuento = Objects.requireNonNull(estrategia);
    }

    public DescuentoStrategy getDiscount() {
        return descuento;
    }

    public void agregar(Producto producto, int cantidad) {
        ElementoPedido elemento = elementos.get(producto.getId());

        if (elemento == null) {
            elementos.put(producto.getId(),
                    new ElementoPedido(producto, cantidad));
        } else {
            elemento.agregarCantidad(cantidad);
        }
    }

    public Collection<ElementoPedido> getElementos() {
        return Collections.unmodifiableCollection(elementos.values());
    }
}

final class DescuentoRegular implements DescuentoStrategy {
    @Override
    public double calcularDescuento(double subtotal) {
        return 0.0;
    }
}

final class DescuentoFrecuente implements DescuentoStrategy {
    @Override
    public double calcularDescuento(double subtotal) {
        return subtotal * 0.10;
    }
}

final class DescuentoMayoreo implements DescuentoStrategy {
    @Override
    public double calcularDescuento(double subtotal) {
        return subtotal >= 5000.0 ? subtotal * 0.15 : 0.0;
    }
}

class ProductoRepositoryImpl implements ProductoRepository {
    private final Map<Integer, Producto> productos =
            new HashMap<Integer, Producto>();

    public ProductoRepositoryImpl() {
    }

    @Override
    public boolean guardar(ProductoDTO dto) {
        if (dto == null || dto.getId() == null
                || dto.getNombre() == null || dto.getPrecio() == null) {
            return false;
        }

        try {
            int id = Integer.parseInt(dto.getId().trim());
            String nombre = dto.getNombre().trim();
            double precio = Double.parseDouble(dto.getPrecio().trim());

            if (id < 1 || id > 1000000000
                    || nombre.isEmpty() || nombre.length() > 80
                    || !Double.isFinite(precio)
                    || precio < 0.01 || precio > 1000000.0
                    || productos.containsKey(id)) {
                return false;
            }

            productos.put(id, new Producto(id, nombre, precio));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Producto buscarPorId(int idProducto) {
        return productos.get(idProducto);
    }

    @Override
    public boolean existe(int idProducto) {
        return productos.containsKey(idProducto);
    }
}

class PedidoServiceImpl implements PedidoService {
    private final ProductoRepository repositorio;

    public PedidoServiceImpl(ProductoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    @Override
    public EstadoAgregarProducto agregarProducto(
            Pedido pedido, int idProducto, int cantidad) {

        Producto producto = repositorio.buscarPorId(idProducto);

        if (producto == null) {
            return EstadoAgregarProducto.PRODUCTO_NO_EXISTE;
        }

        if (cantidad <= 0) {
            return EstadoAgregarProducto.CANTIDAD_INVALIDA;
        }

        pedido.agregar(producto, cantidad);
        return EstadoAgregarProducto.AGREGADO;
    }

    @Override
    public DescuentoStrategy SelectorDescuento(TipoDescuento tipo) {
        switch (tipo) {
            case REGULAR:
                return new DescuentoRegular();
            case FRECUENTE:
                return new DescuentoFrecuente();
            case MAYOREO:
                return new DescuentoMayoreo();
            default:
                throw new IllegalArgumentException(
                        "Tipo de descuento invalido");
        }
    }

    @Override
    public ResumenPedidoDTO confirmarPedido(
            Pedido pedido, TipoDescuento tipo) {

        DescuentoStrategy estrategia = SelectorDescuento(tipo);
        pedido.setDiscount(estrategia);

        List<DetallePedidoDTO> detalles =
                new ArrayList<DetallePedidoDTO>();

        double subtotal = 0.0;

        for (ElementoPedido elemento : pedido.getElementos()) {
            Producto producto = elemento.getProducto();

            detalles.add(new DetallePedidoDTO(
                    producto.getId(),
                    producto.getNombre(),
                    elemento.getCantidad()
            ));

            subtotal += elemento.calcularSubtotal();
        }

        double descuento =
                pedido.getDiscount().calcularDescuento(subtotal);

        return new ResumenPedidoDTO(
                detalles, subtotal, descuento, subtotal - descuento);
    }
}
