

/* INTERFACES */

interface Comunicable {
    String comunicar();
}

interface Rastreable {
    String obtenerPosicion();
}

interface Operable {
    boolean estaOperativo();
}

interface Escaneable {
    String escanear();
}


/* DESTINOS */

abstract class DestinoEspacial {
    private String codigo, nombre;
    private double gravedad;

    public DestinoEspacial(String codigo, String nombre, double gravedad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.gravedad = gravedad;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getGravedad() { return gravedad; }
}

class Planeta extends DestinoEspacial {
    private boolean atmosfera;

    public Planeta(String codigo, String nombre, double gravedad, boolean atmosfera) {
        super(codigo, nombre, gravedad);
        this.atmosfera = atmosfera;
    }

    public boolean tieneAtmosfera() { return atmosfera; }
}

class SateliteNatural extends DestinoEspacial {
    private String orbitaA;

    public SateliteNatural(String codigo, String nombre, double gravedad, String orbitaA) {
        super(codigo, nombre, gravedad);
        this.orbitaA = orbitaA;
    }

    public String getOrbitaA() { return orbitaA; }
}


/* NAVE */

class Nave implements Comunicable, Rastreable, Operable {
    private String id, nombre, posicion;
    private boolean operativa;

    public Nave(String id, String nombre, String posicion, boolean operativa) {
        this.id = id;
        this.nombre = nombre;
        this.posicion = posicion;
        this.operativa = operativa;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String comunicar() { return "Comunicacion establecida"; }
    public String obtenerPosicion() { return posicion; }
    public boolean estaOperativo() { return operativa; }
}


/* RECURSOS */

abstract class Recurso {
    private String id;

    public Recurso(String id) {
        this.id = id;
    }

    public String getId() { return id; }
}

class Dron extends Recurso implements Comunicable, Rastreable, Operable, Escaneable {
    private String modelo, posicion;
    private boolean operativo;

    public Dron(String id, String modelo, String posicion, boolean operativo) {
        super(id);
        this.modelo = modelo;
        this.posicion = posicion;
        this.operativo = operativo;
    }

    public String comunicar() { return "Dron conectado"; }
    public String obtenerPosicion() { return posicion; }
    public boolean estaOperativo() { return operativo; }
    public String escanear() { return "Escaneo realizado"; }
}

class SateliteArtificial extends Recurso implements Comunicable, Rastreable, Operable, Escaneable {
    private String nombre, posicion;
    private boolean operativo;

    public SateliteArtificial(String id, String nombre, String posicion, boolean operativo) {
        super(id);
        this.nombre = nombre;
        this.posicion = posicion;
        this.operativo = operativo;
    }

    public String comunicar() { return "Satelite conectado"; }
    public String obtenerPosicion() { return posicion; }
    public boolean estaOperativo() { return operativo; }
    public String escanear() { return "Escaneo realizado"; }
}

class Carga extends Recurso implements Rastreable {
    private String descripcion, posicion;
    private double peso;

    public Carga(String id, String descripcion, double peso, String posicion) {
        super(id);
        this.descripcion = descripcion;
        this.peso = peso;
        this.posicion = posicion;
    }

    public String obtenerPosicion() { return posicion; }
}

class Tripulante extends Recurso implements Comunicable, Rastreable {
    private String nombre, posicion;

    public Tripulante(String id, String nombre, String posicion) {
        super(id);
        this.nombre = nombre;
        this.posicion = posicion;
    }

    public String comunicar() { return "Tripulante conectado"; }
    public String obtenerPosicion() { return posicion; }
}


/* CLASE PADRE MISION */

abstract class Mision {
    private String id, nombre;
    private Nave nave;
    private DestinoEspacial destino;
    protected List<Recurso> recursos;

    public Mision(String id, String nombre, Nave nave,
                  DestinoEspacial destino, List<Recurso> recursos) {
        this.id = id;
        this.nombre = nombre;
        this.nave = nave;
        this.destino = destino;
        this.recursos = recursos;
    }

    protected abstract boolean validarRecursos();
    protected abstract ResultadoObjetivo ejecutarObjetivo();

    protected EstadoPaso prepararRecursos() {
        return EstadoPaso.OK;
    }

    protected EstadoPaso iniciarViaje() {
        return EstadoPaso.OK;
    }

    protected EstadoPaso finalizarOperacion() {
        return EstadoPaso.OK;
    }

    protected EstadoPaso generarReporte() {
        return EstadoPaso.OK;
    }

    public final EjecucionMisionDTO ejecutarMision() {

        if (!nave.estaOperativo() || !validarRecursos()) {
            return new EjecucionMisionDTO(
                EstadoValidacion.FALLIDA,
                EstadoPaso.NO_EJECUTADO,
                EstadoPaso.NO_EJECUTADO,
                ResultadoObjetivo.NO_EJECUTADO,
                EstadoPaso.NO_EJECUTADO,
                EstadoPaso.NO_EJECUTADO
            );
        }

        EstadoPaso preparacion = prepararRecursos();
        EstadoPaso viaje = iniciarViaje();
        ResultadoObjetivo objetivo = ejecutarObjetivo();
        EstadoPaso finalizacion = finalizarOperacion();
        EstadoPaso reporte = generarReporte();

        return new EjecucionMisionDTO(
            EstadoValidacion.OK,
            preparacion, viaje, objetivo, finalizacion, reporte
        );
    }
}


/* TIPOS DE MISION */

class MisionExploracion extends Mision {

    public MisionExploracion(String id, String nombre, Nave nave,
                            DestinoEspacial destino, List<Recurso> recursos) {
        super(id, nombre, nave, destino, recursos);
    }

    @Override
    protected boolean validarRecursos() {
        return recursos.size() == 1 && recursos.get(0) instanceof Escaneable;
    }

    @Override
    protected ResultadoObjetivo ejecutarObjetivo() {
        Escaneable e = (Escaneable) recursos.get(0);
        e.escanear();
        return ResultadoObjetivo.ESCANEO_COMPLETADO;
    }
}

class MisionRescate extends Mision {

    public MisionRescate(String id, String nombre, Nave nave,
                        DestinoEspacial destino, List<Recurso> recursos) {
        super(id, nombre, nave, destino, recursos);
    }

    @Override
    protected boolean validarRecursos() {
        if (recursos.isEmpty()) {
            return false;
        }

        for (Recurso r : recursos) {
            if (!(r instanceof Tripulante)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected ResultadoObjetivo ejecutarObjetivo() {
        return ResultadoObjetivo.RESCATE_COMPLETADO;
    }
}

class MisionTransporte extends Mision {

    public MisionTransporte(String id, String nombre, Nave nave,
                           DestinoEspacial destino, List<Recurso> recursos) {
        super(id, nombre, nave, destino, recursos);
    }

    @Override
    protected boolean validarRecursos() {
        return recursos.size() == 1 && recursos.get(0) instanceof Carga;
    }

    @Override
    protected ResultadoObjetivo ejecutarObjetivo() {
        return ResultadoObjetivo.CARGA_ENTREGADA;
    }
}


/* MAPPERS */

class DestinoMapperImpl implements DestinoMapper {

    public DestinoEspacial toEntity(DestinoDTO d) {
        if (d.getTipo() == TipoDestino.PLANETA) {
            return new Planeta(d.getCodigo(), d.getNombre(),
                d.getGravedad(), Boolean.parseBoolean(d.getDatoEspecifico()));
        } else {
            return new SateliteNatural(d.getCodigo(), d.getNombre(),
                d.getGravedad(), d.getDatoEspecifico());
        }
    }
}

class NaveMapperImpl implements NaveMapper {

    public Nave toEntity(NaveDTO d) {
        return new Nave(d.getId(), d.getNombre(),
                        d.getPosicion(), d.isOperativa());
    }
}

class RecursoMapperImpl implements RecursoMapper {

    public Recurso toEntity(RecursoDTO dto) {

        if (dto instanceof DronDTO) {
            DronDTO d = (DronDTO) dto;
            return new Dron(d.getId(), d.getModelo(),
                            d.getPosicion(), d.isOperativo());
        }

        if (dto instanceof SateliteArtificialDTO) {
            SateliteArtificialDTO s = (SateliteArtificialDTO) dto;
            return new SateliteArtificial(s.getId(), s.getNombre(),
                                         s.getPosicion(), s.isOperativo());
        }

        if (dto instanceof CargaDTO) {
            CargaDTO c = (CargaDTO) dto;
            return new Carga(c.getId(), c.getDescripcion(),
                             c.getPeso(), c.getPosicion());
        }

        TripulanteDTO t = (TripulanteDTO) dto;
        return new Tripulante(t.getId(), t.getNombre(), t.getPosicion());
    }
}


/* SERVICIO DE MISIONES */

class MisionFactoryServiceImpl implements MisionFactoryService {

    public Mision crearMision(MisionDTO dto, Nave nave,
                             DestinoEspacial destino,
                             Map<String, Recurso> recursos) {

        List<Recurso> lista = new ArrayList<Recurso>();

        for (String id : dto.getIdsRecursos()) {
            lista.add(recursos.get(id));
        }

        if (dto.getTipo() == TipoMision.EXPLORACION) {
            return new MisionExploracion(dto.getId(), dto.getNombre(),
                                        nave, destino, lista);
        }

        if (dto.getTipo() == TipoMision.RESCATE) {
            return new MisionRescate(dto.getId(), dto.getNombre(),
                                    nave, destino, lista);
        }

        return new MisionTransporte(dto.getId(), dto.getNombre(),
                                   nave, destino, lista);
    }
}


/* SERVICIO DE CAPACIDADES */

class CapacidadServiceImpl implements CapacidadService {

    public List<Capacidad> obtenerCapacidades(Object entidad) {

        List<Capacidad> lista = new ArrayList<Capacidad>();

        if (entidad instanceof Comunicable)
            lista.add(Capacidad.COMUNICABLE);

        if (entidad instanceof Rastreable)
            lista.add(Capacidad.RASTREABLE);

        if (entidad instanceof Operable)
            lista.add(Capacidad.OPERABLE);

        if (entidad instanceof Escaneable)
            lista.add(Capacidad.ESCANEABLE);

        return lista;
    }
}
