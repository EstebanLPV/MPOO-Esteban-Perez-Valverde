import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ControlAcceso {
    public static void main(String[] args) throws Exception {
        BufferedReader lector = new BufferedReader(
                new InputStreamReader(System.in));

        String asistentes = lector.readLine();
        String operaciones = lector.readLine();

        ControlAccesoService servicio =
                new ControlAccesoServiceImpl();

        if (asistentes != null
                && !asistentes.trim().isEmpty()
                && !asistentes.trim().equals("-")) {

            for (String registro : asistentes.split(";")) {
                String[] datos = registro.split("\\|", 3);

                if (datos.length == 3) {
                    servicio.registrarAsistente(
                            new AsistenteDTO(
                                    datos[0].trim(),
                                    datos[1].trim(),
                                    datos[2].trim()
                            )
                    );
                }
            }
        }

        StringBuilder salida = new StringBuilder();

        if (operaciones != null
                && !operaciones.trim().isEmpty()
                && !operaciones.trim().equals("-")) {

            for (String operacion : operaciones.split(";")) {
                String[] datos = operacion.split("\\|", 2);

                if (datos.length != 2) {
                    continue;
                }

                String accion = datos[0].trim();

                if (accion.equals("ENTRADA")) {
                    int id = Integer.parseInt(datos[1].trim());

                    salida.append("ENTRADA ")
                            .append(id)
                            .append(" ")
                            .append(servicio.registrarEntrada(id))
                            .append("\n");

                } else if (accion.equals("SALIDA")) {
                    int id = Integer.parseInt(datos[1].trim());

                    salida.append("SALIDA ")
                            .append(id)
                            .append(" ")
                            .append(servicio.registrarSalida(id))
                            .append("\n");

                } else if (accion.equals("MASIVA")) {
                    List<Integer> identificadores =
                            new ArrayList<Integer>();

                    for (String id : datos[1].split(",")) {
                        identificadores.add(
                                Integer.parseInt(id.trim()));
                    }

                    List<ResultadoEntradaDTO> resultados =
                            servicio.registrarEntradasMasivas(
                                    identificadores);

                    for (ResultadoEntradaDTO resultado : resultados) {
                        salida.append("ENTRADA ")
                                .append(resultado.getIdAsistente())
                                .append(" ")
                                .append(resultado.getEstado())
                                .append("\n");
                    }
                }
            }
        }

        ReporteDTO reporte = servicio.generarReporte();

        salida.append(String.format(
                Locale.US,
                "REPORTE %d %d %d %d %d %d %.2f",
                reporte.getRegistrados(),
                reporte.getDisponibles(),
                reporte.getAforo(),
                reporte.getAlumnos(),
                reporte.getProfesores(),
                reporte.getInvitados(),
                reporte.getOcupacion()
        ));

        System.out.println(salida);
    }
}

enum TipoAsistente {
    ALUMNO, PROFESOR, INVITADO
}

enum EstadoEntrada {
    AUTORIZADO, NO_REGISTRADO, YA_DENTRO, AFORO_COMPLETO
}

enum EstadoSalida {
    AUTORIZADA, NO_REGISTRADO, NO_ESTA_DENTRO
}

final class Asistente {
    private final int id;
    private final String nombre;
    private final TipoAsistente tipo;

    public Asistente(int id, String nombre, TipoAsistente tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoAsistente getTipo() {
        return tipo;
    }
}

final class AsistenteDTO {
    private final String id;
    private final String nombre;
    private final String tipo;

    public AsistenteDTO(String id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }
}

final class ResultadoEntradaDTO {
    private final int idAsistente;
    private final EstadoEntrada estado;

    public ResultadoEntradaDTO(
            int idAsistente,
            EstadoEntrada estado) {

        this.idAsistente = idAsistente;
        this.estado = estado;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public EstadoEntrada getEstado() {
        return estado;
    }
}

final class ReporteDTO {
    private final int registrados;
    private final int disponibles;
    private final int aforo;
    private final int alumnos;
    private final int profesores;
    private final int invitados;
    private final double ocupacion;

    public ReporteDTO(
            int registrados,
            int disponibles,
            int aforo,
            int alumnos,
            int profesores,
            int invitados,
            double ocupacion) {

        this.registrados = registrados;
        this.disponibles = disponibles;
        this.aforo = aforo;
        this.alumnos = alumnos;
        this.profesores = profesores;
        this.invitados = invitados;
        this.ocupacion = ocupacion;
    }

    public int getRegistrados() {
        return registrados;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public int getAforo() {
        return aforo;
    }

    public int getAlumnos() {
        return alumnos;
    }

    public int getProfesores() {
        return profesores;
    }

    public int getInvitados() {
        return invitados;
    }

    public double getOcupacion() {
        return ocupacion;
    }
}

interface ControlAccesoService {
    int AFORO_MAXIMO = 10;

    boolean registrarAsistente(AsistenteDTO asistente);

    EstadoEntrada registrarEntrada(int idAsistente);

    EstadoSalida registrarSalida(int idAsistente);

    List<ResultadoEntradaDTO> registrarEntradasMasivas(
            List<Integer> identificadores);

    ReporteDTO generarReporte();
}

class ControlAccesoServiceImpl implements ControlAccesoService {
    private static final Map<Integer, Asistente> registrados =
            new HashMap<Integer, Asistente>();

    private static final Set<Integer> dentro =
            new HashSet<Integer>();

    @Override
    public boolean registrarAsistente(AsistenteDTO dto) {
        if (dto == null
                || dto.getId() == null
                || dto.getNombre() == null
                || dto.getTipo() == null) {
            return false;
        }

        try {
            int id = Integer.parseInt(dto.getId().trim());
            String nombre = dto.getNombre().trim();

            TipoAsistente tipo =
                    TipoAsistente.valueOf(dto.getTipo().trim());

            if (id < 1
                    || id > 1000000000
                    || nombre.isEmpty()
                    || nombre.length() > 80
                    || registrados.containsKey(id)) {
                return false;
            }

            registrados.put(
                    id,
                    new Asistente(id, nombre, tipo)
            );

            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public EstadoEntrada registrarEntrada(int idAsistente) {
        if (!registrados.containsKey(idAsistente)) {
            return EstadoEntrada.NO_REGISTRADO;
        }

        if (dentro.contains(idAsistente)) {
            return EstadoEntrada.YA_DENTRO;
        }

        if (dentro.size() >= AFORO_MAXIMO) {
            return EstadoEntrada.AFORO_COMPLETO;
        }

        dentro.add(idAsistente);
        return EstadoEntrada.AUTORIZADO;
    }

    @Override
    public EstadoSalida registrarSalida(int idAsistente) {
        if (!registrados.containsKey(idAsistente)) {
            return EstadoSalida.NO_REGISTRADO;
        }

        if (!dentro.remove(idAsistente)) {
            return EstadoSalida.NO_ESTA_DENTRO;
        }

        return EstadoSalida.AUTORIZADA;
    }

    @Override
    public List<ResultadoEntradaDTO> registrarEntradasMasivas(
            List<Integer> identificadores) {

        List<ResultadoEntradaDTO> resultados =
                new ArrayList<ResultadoEntradaDTO>();

        for (int id : identificadores) {
            if (dentro.size() >= AFORO_MAXIMO) {
                break;
            }

            resultados.add(
                    new ResultadoEntradaDTO(
                            id,
                            registrarEntrada(id)
                    )
            );
        }

        return resultados;
    }

    @Override
    public ReporteDTO generarReporte() {
        int alumnos = 0;
        int profesores = 0;
        int invitados = 0;

        for (int id : dentro) {
            TipoAsistente tipo = registrados.get(id).getTipo();

            switch (tipo) {
                case ALUMNO:
                    alumnos++;
                    break;

                case PROFESOR:
                    profesores++;
                    break;

                case INVITADO:
                    invitados++;
                    break;
            }
        }

        int aforo = dentro.size();
        double ocupacion = aforo * 100.0 / AFORO_MAXIMO;

        return new ReporteDTO(
                registrados.size(),
                AFORO_MAXIMO - aforo,
                aforo,
                alumnos,
                profesores,
                invitados,
                ocupacion
        );
    }
}
