
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

enum CategoriaProducto {
    BEBIDA,
    SNACK
}

enum ResultadoRegistro {
    PRODUCTO_REGISTRADO,
    CODIGO_DUPLICADO,
    PRODUCTO_INVALIDO,
    PRECIO_INVALIDO
}

enum ResultadoReabastecimiento {
    REABASTECIMIENTO_REALIZADO,
    PRODUCTO_NO_ENCONTRADO,
    CANTIDAD_INVALIDA
}

enum ResultadoEntrega {
    PRODUCTO_PREPARADO,
    PRODUCTO_NO_ENCONTRADO,
    SIN_EXISTENCIAS
}

enum EstadoCobro {
    COBRO_REALIZADO,
    MONTO_INVALIDO,
    SIN_FONDOS_SUFiCIENTES
}

enum EstadoCompra {
    VENTA_REALIZADA,
    PRODUCTO_NO_ENCONTRADO,
    SIN_EXISTENCIAS,
    PAGO_INSUFICIENTE,
    SIN_FONDOS_SUFICIENTES
}

class Producto {
    private final String codigo;
    private final String nombre;
    private final LocalDate fechaCaducidad;
    private final CategoriaProducto categoria;

    Producto(String codigo, String nombre,
             LocalDate fechaCaducidad,
             CategoriaProducto categoria) {
        this.codigo = Objects.requireNonNull(codigo);
        this.nombre = Objects.requireNonNull(nombre);
        this.fechaCaducidad = Objects.requireNonNull(fechaCaducidad);
        this.categoria = Objects.requireNonNull(categoria);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    protected String descripcionBase() {
        return codigo + " - " + nombre;
    }
}

class Bebida extends Producto {
    Bebida(String codigo, String nombre,
           LocalDate fechaCaducidad) {
        super(codigo, nombre, fechaCaducidad,
              CategoriaProducto.BEBIDA);
    }

    public String describir() {
        return descripcionBase() + " (bebida)";
    }
}

class Snack extends Producto {
    Snack(String codigo, String nombre,
          LocalDate fechaCaducidad) {
        super(codigo, nombre, fechaCaducidad,
              CategoriaProducto.SNACK);
    }

    public String describir() {
        return descripcionBase() + " (snack)";
    }
}

record ProductoInventario(
    Producto producto,
    double precio,
    int existencias
) {}

record ResultadoCobro(
    EstadoCobro estado,
    double cambio
) {}

record ResultadoCompra(
    EstadoCompra estado,
    Producto producto,
    double cambio
) {}

record RegistroProductoDTO(
    String codigo,
    String nombre,
    LocalDate fechaCaducidad,
    CategoriaProducto categoria,
    double precio
) {}

record ReabastecimientoDTO(
    String codigo,
    int cantidad
) {}

record CompraDTO(
    String codigoProducto,
    double cantidadRecibida
) {}

interface Inventario {
    ResultadoRegistro registrarProducto(
        Producto producto,
        double precio
    );

    Optional<ProductoInventario> buscarProducto(
        String codigo
    );

    ResultadoReabastecimiento reabastecer(
        String codigo,
        int cantidad
    );

    ResultadoEntrega despachar(
        String codigo
    );
}

interface Monedero {
    ResultadoCobro cobrar(
        double precio,
        double cantidadRecibida
    );

    double consultarFondos();
}

interface MaquinaDespachadora {
    ResultadoRegistro registrarProducto(
        RegistroProductoDTO producto
    );

    Optional<ProductoInventario> buscarProducto(
        String codigo
    );

    ResultadoReabastecimiento reabastecer(
        ReabastecimientoDTO solicitud
    );

    ResultadoCompra comprar(
        CompraDTO solicitud
    );

    double consultarFondos();
}

class InventarioImpl implements Inventario {

    private static class Entrada {
        final Producto producto;
        final double precio;
        int existencias;

        Entrada(Producto producto, double precio) {
            this.producto = producto;
            this.precio = precio;
            this.existencias = 0;
        }
    }

    private final Map<String, Entrada> productos =
            new HashMap<>();

    @Override
    public ResultadoRegistro registrarProducto(
            Producto producto, double precio) {

        if (producto == null
                || producto.getCodigo().isBlank()
                || producto.getNombre().isBlank()) {
            return ResultadoRegistro.PRODUCTO_INVALIDO;
        }

        if (!Double.isFinite(precio) || precio <= 0) {
            return ResultadoRegistro.PRECIO_INVALIDO;
        }

        if (productos.containsKey(producto.getCodigo())) {
            return ResultadoRegistro.CODIGO_DUPLICADO;
        }

        productos.put(
            producto.getCodigo(),
            new Entrada(producto, precio)
        );

        return ResultadoRegistro.PRODUCTO_REGISTRADO;
    }

    @Override
    public Optional<ProductoInventario> buscarProducto(
            String codigo) {

        Entrada e = productos.get(codigo);

        if (e == null) {
            return Optional.empty();
        }

        return Optional.of(
            new ProductoInventario(
                e.producto,
                e.precio,
                e.existencias
            )
        );
    }

    @Override
    public ResultadoReabastecimiento reabastecer(
            String codigo, int cantidad) {

        if (cantidad <= 0) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }

        Entrada e = productos.get(codigo);

        if (e == null) {
            return ResultadoReabastecimiento.PRODUCTO_NO_ENCONTRADO;
        }

        if (e.existencias > Integer.MAX_VALUE - cantidad) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }

        e.existencias += cantidad;

        return ResultadoReabastecimiento.REABASTECIMIENTO_REALIZADO;
    }

    @Override
    public ResultadoEntrega despachar(String codigo) {

        Entrada e = productos.get(codigo);

        if (e == null) {
            return ResultadoEntrega.PRODUCTO_NO_ENCONTRADO;
        }

        if (e.existencias == 0) {
            return ResultadoEntrega.SIN_EXISTENCIAS;
        }

        e.existencias--;

        return ResultadoEntrega.PRODUCTO_PREPARADO;
    }
}

class MonederoImpl implements Monedero {
    private double fondos;

    MonederoImpl(double fondosIniciales) {

        if (!Double.isFinite(fondosIniciales)
                || fondosIniciales < 0) {
            throw new IllegalArgumentException(
                "Fondos iniciales invalidos"
            );
        }

        this.fondos = fondosIniciales;
    }

    @Override
    public ResultadoCobro cobrar(
            double precio, double cantidadRecibida) {

        if (!Double.isFinite(precio)
                || !Double.isFinite(cantidadRecibida)
                || precio <= 0
                || cantidadRecibida < 0) {

            return new ResultadoCobro(
                EstadoCobro.MONTO_INVALIDO, 0
            );
        }

        if (cantidadRecibida < precio) {
            return new ResultadoCobro(
                EstadoCobro.SIN_FONDOS_SUFiCIENTES, 0
            );
        }

        fondos += precio;

        return new ResultadoCobro(
            EstadoCobro.COBRO_REALIZADO,
            cantidadRecibida - precio
        );
    }

    @Override
    public double consultarFondos() {
        return fondos;
    }
}

class MaquinaExpendedora implements MaquinaDespachadora {
    private final Inventario inventario;
    private final Monedero monedero;

    private MaquinaExpendedora(Builder builder) {
        this.inventario = builder.inventario;
        this.monedero = builder.monedero;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private Inventario inventario;
        private Monedero monedero;

        Builder inventario(Inventario inventario) {
            this.inventario = inventario;
            return this;
        }

        Builder monedero(Monedero monedero) {
            this.monedero = monedero;
            return this;
        }

        MaquinaExpendedora build() {
            Objects.requireNonNull(
                inventario, "Falta Inventario"
            );

            Objects.requireNonNull(
                monedero, "Falta Monedero"
            );

            return new MaquinaExpendedora(this);
        }
    }

    @Override
    public ResultadoRegistro registrarProducto(
            RegistroProductoDTO dto) {

        if (dto == null || dto.categoria() == null) {
            return ResultadoRegistro.PRODUCTO_INVALIDO;
        }

        Producto producto = switch (dto.categoria()) {
            case BEBIDA -> new Bebida(
                dto.codigo(),
                dto.nombre(),
                dto.fechaCaducidad()
            );

            case SNACK -> new Snack(
                dto.codigo(),
                dto.nombre(),
                dto.fechaCaducidad()
            );
        };

        return inventario.registrarProducto(
            producto, dto.precio()
        );
    }

    @Override
    public Optional<ProductoInventario> buscarProducto(
            String codigo) {
        return inventario.buscarProducto(codigo);
    }

    @Override
    public ResultadoReabastecimiento reabastecer(
            ReabastecimientoDTO dto) {

        if (dto == null) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }

        return inventario.reabastecer(
            dto.codigo(),
            dto.cantidad()
        );
    }

    @Override
    public ResultadoCompra comprar(CompraDTO solicitud) {

        if (solicitud == null) {
            return new ResultadoCompra(
                EstadoCompra.PAGO_INSUFICIENTE,
                null,
                0
            );
        }

        Optional<ProductoInventario> hallado =
            inventario.buscarProducto(
                solicitud.codigoProducto()
            );

        if (hallado.isEmpty()) {
            return new ResultadoCompra(
                EstadoCompra.PRODUCTO_NO_ENCONTRADO,
                null,
                0
            );
        }

        ProductoInventario item = hallado.get();

        if (item.existencias() <= 0) {
            return new ResultadoCompra(
                EstadoCompra.SIN_EXISTENCIAS,
                item.producto(),
                0
            );
        }

        double pago = solicitud.cantidadRecibida();

        if (!Double.isFinite(pago)
                || pago < item.precio()) {
            return new ResultadoCompra(
                EstadoCompra.PAGO_INSUFICIENTE,
                item.producto(),
                0
            );
        }

        ResultadoEntrega entrega = inventario.despachar(
            solicitud.codigoProducto()
        );

        if (entrega != ResultadoEntrega.PRODUCTO_PREPARADO) {
            return new ResultadoCompra(
                EstadoCompra.SIN_EXISTENCIAS,
                item.producto(),
                0
            );
        }

        ResultadoCobro cobro = monedero.cobrar(
            item.precio(),
            pago
        );

        if (cobro.estado() != EstadoCobro.COBRO_REALIZADO) {
            inventario.reabastecer(
                solicitud.codigoProducto(), 1
            );

            return new ResultadoCompra(
                EstadoCompra.SIN_FONDOS_SUFICIENTES,
                item.producto(),
                0
            );
        }

        return new ResultadoCompra(
            EstadoCompra.VENTA_REALIZADA,
            item.producto(),
            cobro.cambio()
        );
    }

    @Override
    public double consultarFondos() {
        return monedero.consultarFondos();
    }
}

class Result {
    public static MaquinaDespachadora crearMaquina(
            double fondosIniciales) {

        Inventario inventario = new InventarioImpl();
        Monedero monedero = new MonederoImpl(
            fondosIniciales
        );

        return MaquinaExpendedora.builder()
            .inventario(inventario)
            .monedero(monedero)
            .build();
    }
}

public class Solution {

    public static void main(String[] args) {

        MaquinaDespachadora maquina =
            Result.crearMaquina(100);

        LocalDate caducidad =
            LocalDate.of(2027, 12, 31);

        String[] codigos = {
            "AGUA01",
            "JUGO01",
            "PAPAS01",
            "GALLETAS01"
        };

        String[] nombres = {
            "Agua",
            "Jugo",
            "Papas",
            "Galletas"
        };

        CategoriaProducto[] tipos = {
            CategoriaProducto.BEBIDA,
            CategoriaProducto.BEBIDA,
            CategoriaProducto.SNACK,
            CategoriaProducto.SNACK
        };

        double[] precios = {
            15, 18, 20, 12
        };

        System.out.println(
            "=== SmartVend / prueba local ==="
        );

        for (int i = 0; i < codigos.length; i++) {

            ResultadoRegistro r =
                maquina.registrarProducto(
                    new RegistroProductoDTO(
                        codigos[i],
                        nombres[i],
                        caducidad,
                        tipos[i],
                        precios[i]
                    )
                );

            System.out.println(
                "REGISTRAR " + codigos[i] + ": " + r
            );
        }

        System.out.println(
            "REABASTECER AGUA01: " +
            maquina.reabastecer(
                new ReabastecimientoDTO("AGUA01", 5)
            )
        );

        System.out.println(
            "REABASTECER JUGO01: " +
            maquina.reabastecer(
                new ReabastecimientoDTO("JUGO01", 3)
            )
        );

        System.out.println(
            "REABASTECER GALLETAS01: " +
            maquina.reabastecer(
                new ReabastecimientoDTO("GALLETAS01", 2)
            )
        );

        System.out.println(
            "BUSCAR AGUA01: " +
            maquina.buscarProducto("AGUA01")
                .map(p ->
                    p.precio() + " / " +
                    p.existencias() + " unidades"
                )
                .orElse("No encontrado")
        );

        ResultadoCompra venta = maquina.comprar(
            new CompraDTO("AGUA01", 20)
        );

        System.out.println(
            "VENTA AGUA01: " + venta.estado() +
            " | cambio=" + venta.cambio()
        );

        ResultadoCompra sinStock = maquina.comprar(
            new CompraDTO("PAPAS01", 30)
        );

        System.out.println(
            "VENTA PAPAS01: " + sinStock.estado()
        );

        ResultadoCompra sinPago = maquina.comprar(
            new CompraDTO("JUGO01", 5)
        );

        System.out.println(
            "VENTA JUGO01: " + sinPago.estado()
        );

        System.out.println(
            "FONDOS: " + maquina.consultarFondos()
        );

        System.out.println(
            "STOCK AGUA01: " +
            maquina.buscarProducto("AGUA01")
                .orElseThrow()
                .existencias()
        );
    }
}
