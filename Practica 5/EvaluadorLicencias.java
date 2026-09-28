import java.io.*;
import java.text.*;
import java.util.*;

enum TipoLicencia {
    BASICA(1000.00, Calendar.YEAR, 1),
    PROFESIONAL(1500.00, Calendar.YEAR, 2),
    EMPRESARIAL(2500.00, Calendar.YEAR, 3),
    TEMPORAL(600.00, Calendar.MONTH, 6);

    final double costoBase;
    final int unidadPeriodo;
    final int cantidadPeriodo;

    TipoLicencia(double costoBase, int unidadPeriodo, int cantidadPeriodo) {
        this.costoBase = costoBase;
        this.unidadPeriodo = unidadPeriodo;
        this.cantidadPeriodo = cantidadPeriodo;
    }
}

enum EstadoLicencia {
    VIGENTE,
    PROXIMA_A_VENCER,
    VENCIDA,
    BLOQUEADA
}

class Result {

    public static String evaluarLicencia(
            String fechaActual,
            String fechaVencimiento,
            String tipoLicencia,
            int renovacionesPrevias) {

        try {
            TimeZone zona = TimeZone.getTimeZone("UTC");

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            formato.setLenient(false);
            formato.setTimeZone(zona);

            Calendar actual = Calendar.getInstance(zona);
            actual.setTime(formato.parse(fechaActual));

            Calendar vencimiento = Calendar.getInstance(zona);
            vencimiento.setTime(formato.parse(fechaVencimiento));

            long diferenciaMillis =
                vencimiento.getTimeInMillis() - actual.getTimeInMillis();

            long diferenciaDias =
                diferenciaMillis / (24L * 60 * 60 * 1000);

            EstadoLicencia estado;

            if (diferenciaDias > 30) {
                estado = EstadoLicencia.VIGENTE;
            } else if (diferenciaDias >= 0) {
                estado = EstadoLicencia.PROXIMA_A_VENCER;
            } else if (diferenciaDias >= -90) {
                estado = EstadoLicencia.VENCIDA;
            } else {
                estado = EstadoLicencia.BLOQUEADA;
            }

            if (estado == EstadoLicencia.BLOQUEADA) {
                return "BLOQUEADA " + diferenciaDias
                    + " 0.00 NO_DISPONIBLE";
            }

            TipoLicencia tipo = TipoLicencia.valueOf(tipoLicencia);
            double costo = tipo.costoBase;

            if (estado == EstadoLicencia.VIGENTE) {
                costo *= 0.90;
            } else if (estado == EstadoLicencia.VENCIDA) {
                costo *= 1.20;
            }

            if (renovacionesPrevias > 3) {
                costo *= 0.95;
            }

            Calendar nuevaFecha;

            if (estado == EstadoLicencia.VENCIDA) {
                nuevaFecha = (Calendar) actual.clone();
            } else {
                nuevaFecha = (Calendar) vencimiento.clone();
            }

            nuevaFecha.add(tipo.unidadPeriodo, tipo.cantidadPeriodo);

            return String.format(
                Locale.US,
                "%s %d %.2f %s",
                estado,
                diferenciaDias,
                costo,
                formato.format(nuevaFecha.getTime())
            );

        } catch (ParseException e) {
            return "INVALID";
        }
    }
}

public class EvaluadorLicencias {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(System.in));

        String fechaActual = bufferedReader.readLine();
        String fechaVencimiento = bufferedReader.readLine();
        String tipoLicencia = bufferedReader.readLine();
        int renovacionesPrevias =
            Integer.parseInt(bufferedReader.readLine());

        String resultado = Result.evaluarLicencia(
            fechaActual,
            fechaVencimiento,
            tipoLicencia,
            renovacionesPrevias
        );

        System.out.println(resultado);
        bufferedReader.close();
    }
}
