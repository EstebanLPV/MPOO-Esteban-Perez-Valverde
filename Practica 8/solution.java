import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

// TIPOS Y DATOS DE ENTRADA Y SALIDA

enum TipoReembolso {
    ALIMENTOS,
    TRANSPORTE,
    HOSPEDAJE
}

enum ResultadoValidacion {
    APROBADO,
    SIN_COMPROBANTE,
    FUERA_DE_PERIODO,
    EXCEDE_LIMITE,
    REQUIERE_AUTORIZACION
}

final class ReembolsoInputDTO {
    private final TipoReembolso tipo;
    private final String folio;
    private final String empleado;
    private final double monto;
    private final LocalDate fechaGasto;
    private final boolean tieneComprobante;
    private final boolean autorizadoGerente;
    private final LocalDate fechaEvaluacion;

    public ReembolsoInputDTO(
            TipoReembolso tipo, String folio, String empleado,
            double monto, LocalDate fechaGasto,
            boolean tieneComprobante, boolean autorizadoGerente,
            LocalDate fechaEvaluacion) {
        this.tipo = tipo;
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public TipoReembolso getTipo() { return tipo; }
    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }
    public LocalDate getFechaEvaluacion() { return fechaEvaluacion; }
}

final class ReembolsoOutputDTO {
    private final String folio;
    private final TipoReembolso tipo;
    private final ResultadoValidacion resultado;

    public ReembolsoOutputDTO(
            String folio, TipoReembolso tipo,
            ResultadoValidacion resultado) {
        this.folio = folio;
        this.tipo = tipo;
        this.resultado = resultado;
    }

    public String getFolio() { return folio; }
    public TipoReembolso getTipo() { return tipo; }
    public ResultadoValidacion getResultado() { return resultado; }
}

// CLASE BASE Y TIPOS DE REEMBOLSO

abstract class Reembolso {
    private final String folio;
    private final String empleado;
    private final double monto;
    private final LocalDate fechaGasto;
    private final boolean tieneComprobante;
    private final boolean autorizadoGerente;

    // Constructor simplificado: delega al constructor completo.
    protected Reembolso(
            String folio, String empleado,
            double monto, LocalDate fechaGasto) {
        this(folio, empleado, monto, fechaGasto, false, false);
    }

    // Constructor completo: inicializa todos los datos comunes.
    protected Reembolso(
            String folio, String empleado,
            double monto, LocalDate fechaGasto,
            boolean tieneComprobante, boolean autorizadoGerente) {
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
    }

    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }

    public abstract double getLimitePermitido();
}

class ReembolsoAlimentos extends Reembolso {
    public ReembolsoAlimentos(
            String folio, String empleado,
            double monto, LocalDate fechaGasto) {
        super(folio, empleado, monto, fechaGasto);
    }

    public ReembolsoAlimentos(
            String folio, String empleado,
            double monto, LocalDate fechaGasto,
            boolean tieneComprobante, boolean autorizadoGerente) {
        super(folio, empleado, monto, fechaGasto,
                tieneComprobante, autorizadoGerente);
    }

    @Override
    public double getLimitePermitido() {
        return 1000.0;
    }
}

class ReembolsoTransporte extends Reembolso {
    public ReembolsoTransporte(
            String folio, String empleado,
            double monto, LocalDate fechaGasto) {
        super(folio, empleado, monto, fechaGasto);
    }

    public ReembolsoTransporte(
            String folio, String empleado,
            double monto, LocalDate fechaGasto,
            boolean tieneComprobante, boolean autorizadoGerente) {
        super(folio, empleado, monto, fechaGasto,
                tieneComprobante, autorizadoGerente);
    }

    @Override
    public double getLimitePermitido() {
        return 1500.0;
    }
}

class ReembolsoHospedaje extends Reembolso {
    public ReembolsoHospedaje(
            String folio, String empleado,
            double monto, LocalDate fechaGasto) {
        super(folio, empleado, monto, fechaGasto);
    }

    public ReembolsoHospedaje(
            String folio, String empleado,
            double monto, LocalDate fechaGasto,
            boolean tieneComprobante, boolean autorizadoGerente) {
        super(folio, empleado, monto, fechaGasto,
                tieneComprobante, autorizadoGerente);
    }

    @Override
    public double getLimitePermitido() {
        return 4000.0;
    }
}

// FACTORY METHOD

abstract class RegistroReembolso {
    public abstract Reembolso crearReembolso(ReembolsoInputDTO input);
}

class RegistroAlimentos extends RegistroReembolso {
    @Override
    public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoAlimentos(
                input.getFolio(), input.getEmpleado(),
                input.getMonto(), input.getFechaGasto(),
                input.tieneComprobante(), input.estaAutorizadoGerente());
    }
}

class RegistroTransporte extends RegistroReembolso {
    @Override
    public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoTransporte(
                input.getFolio(), input.getEmpleado(),
                input.getMonto(), input.getFechaGasto(),
                input.tieneComprobante(), input.estaAutorizadoGerente());
    }
}

class RegistroHospedaje extends RegistroReembolso {
    @Override
    public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoHospedaje(
                input.getFolio(), input.getEmpleado(),
                input.getMonto(), input.getFechaGasto(),
                input.tieneComprobante(), input.estaAutorizadoGerente());
    }
}

// CHAIN OF RESPONSIBILITY

abstract class ValidadorReembolso {
    private ValidadorReembolso siguiente;

    public ValidadorReembolso enlazarCon(ValidadorReembolso siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    protected ResultadoValidacion continuar(Reembolso reembolso) {
        if (siguiente == null) {
            return ResultadoValidacion.APROBADO;
        }
        return siguiente.validar(reembolso);
    }

    public abstract ResultadoValidacion validar(Reembolso reembolso);
}

class ValidadorComprobante extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso) {
        if (!reembolso.tieneComprobante()) {
            return ResultadoValidacion.SIN_COMPROBANTE;
        }
        return continuar(reembolso);
    }
}

class ValidadorPeriodo extends ValidadorReembolso {
    private final LocalDate fechaEvaluacion;

    public ValidadorPeriodo(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    @Override
    public ResultadoValidacion validar(Reembolso reembolso) {
        long dias = ChronoUnit.DAYS.between(
                reembolso.getFechaGasto(), fechaEvaluacion);

        if (dias > 30) {
            return ResultadoValidacion.FUERA_DE_PERIODO;
        }
        return continuar(reembolso);
    }
}

class ValidadorLimite extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso) {
        if (reembolso.getMonto() > reembolso.getLimitePermitido()) {
            return ResultadoValidacion.EXCEDE_LIMITE;
        }
        return continuar(reembolso);
    }
}

class ValidadorGerencia extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso) {
        if (reembolso.getMonto() > 2000.0
                && !reembolso.estaAutorizadoGerente()) {
            return ResultadoValidacion.REQUIERE_AUTORIZACION;
        }
        return continuar(reembolso);
    }
}

// PROCESAMIENTO

class Result {
    private static final Map<TipoReembolso, RegistroReembolso> REGISTROS =
            crearRegistros();

    private static Map<TipoReembolso, RegistroReembolso> crearRegistros() {
        Map<TipoReembolso, RegistroReembolso> registros =
                new EnumMap<TipoReembolso, RegistroReembolso>(
                        TipoReembolso.class);

        registros.put(TipoReembolso.ALIMENTOS, new RegistroAlimentos());
        registros.put(TipoReembolso.TRANSPORTE, new RegistroTransporte());
        registros.put(TipoReembolso.HOSPEDAJE, new RegistroHospedaje());
        return registros;
    }

    public static ReembolsoOutputDTO procesarReembolso(
            ReembolsoInputDTO input) {
        RegistroReembolso registro = REGISTROS.get(input.getTipo());
        Reembolso reembolso = registro.crearReembolso(input);

        ValidadorReembolso cadena = new ValidadorComprobante();
        cadena.enlazarCon(new ValidadorPeriodo(input.getFechaEvaluacion()))
                .enlazarCon(new ValidadorLimite())
                .enlazarCon(new ValidadorGerencia());

        ResultadoValidacion resultado = cadena.validar(reembolso);

        return new ReembolsoOutputDTO(
                reembolso.getFolio(), input.getTipo(), resultado);
    }
}

// PROGRAMA PRINCIPAL

public class Solution {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));

        LocalDate fechaEvaluacion = LocalDate.parse(br.readLine().trim());
        String[] datos = br.readLine().split("\\|");

        ReembolsoInputDTO input = new ReembolsoInputDTO(
                TipoReembolso.valueOf(datos[0].trim()),
                datos[1].trim(),
                datos[2].trim(),
                Double.parseDouble(datos[3].trim()),
                LocalDate.parse(datos[4].trim()),
                Boolean.parseBoolean(datos[5].trim()),
                Boolean.parseBoolean(datos[6].trim()),
                fechaEvaluacion);

        ReembolsoOutputDTO output = Result.procesarReembolso(input);

        System.out.println("REEMBOLSO " + output.getFolio());
        System.out.println("TIPO " + output.getTipo());
        System.out.println("RESULTADO " + output.getResultado());
    }
}
