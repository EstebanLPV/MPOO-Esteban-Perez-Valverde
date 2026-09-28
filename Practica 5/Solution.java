import java.io.*;
import java.text.*;
import java.util.*;

enum TipoVehiculo {
    MOTOCICLETA(15.00, 100.00),
    AUTOMOVIL(25.00, 180.00),
    CAMIONETA(35.00, 250.00),
    ELECTRICO(20.00, 150.00);

    final double tarifaHora;
    final double maximo24Horas;

    TipoVehiculo(double tarifaHora, double maximo24Horas) {
        this.tarifaHora = tarifaHora;
        this.maximo24Horas = maximo24Horas;
    }
}

class Result {

    public static String calcularEstancia(
            String tipoVehiculo,
            String fechaEntrada,
            String horaEntrada,
            String fechaSalida,
            String horaSalida) {

        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formato.setLenient(false);

            Calendar entrada = Calendar.getInstance();
            entrada.setTime(formato.parse(fechaEntrada + " " + horaEntrada));

            Calendar salida = Calendar.getInstance();
            salida.setTime(formato.parse(fechaSalida + " " + horaSalida));

            long duracionMinutos =
                (salida.getTimeInMillis() - entrada.getTimeInMillis()) / 60000;

            if (duracionMinutos <= 0) {
                return "INVALID";
            }

            TipoVehiculo tipo = TipoVehiculo.valueOf(tipoVehiculo);

            long horasCobradas = (duracionMinutos + 59) / 60;

            long bloquesCompletos = duracionMinutos / 1440;
            long minutosRestantes = duracionMinutos % 1440;
            long horasRestantes = (minutosRestantes + 59) / 60;

            double costo = bloquesCompletos * tipo.maximo24Horas;
            costo += Math.min(
                horasRestantes * tipo.tarifaHora,
                tipo.maximo24Horas
            );

            int diaEntrada = entrada.get(Calendar.DAY_OF_WEEK);
            int diaSalida = salida.get(Calendar.DAY_OF_WEEK);

            boolean finDeSemana =
                diaEntrada == Calendar.SATURDAY ||
                diaEntrada == Calendar.SUNDAY ||
                diaSalida == Calendar.SATURDAY ||
                diaSalida == Calendar.SUNDAY;

            boolean fechaDiferente =
                entrada.get(Calendar.YEAR) != salida.get(Calendar.YEAR) ||
                entrada.get(Calendar.DAY_OF_YEAR) != salida.get(Calendar.DAY_OF_YEAR);

            boolean nocturna =
                entrada.get(Calendar.HOUR_OF_DAY) >= 20 ||
                salida.get(Calendar.HOUR_OF_DAY) < 6 ||
                fechaDiferente;

            if (finDeSemana) {
                costo *= 1.20;
            }

            if (nocturna) {
                costo *= 1.15;
            }

            if (tipo == TipoVehiculo.ELECTRICO) {
                costo *= 0.90;
            }

            String tipoEstancia;

            if (finDeSemana && nocturna) {
                tipoEstancia = "MIXTA";
            } else if (finDeSemana) {
                tipoEstancia = "FIN_SEMANA";
            } else if (nocturna) {
                tipoEstancia = "NOCTURNA";
            } else {
                tipoEstancia = "NORMAL";
            }

            return String.format(
                Locale.US,
                "%d %.2f %s",
                horasCobradas,
                costo,
                tipoEstancia
            );

        } catch (ParseException e) {
            return "INVALID";
        }
    }
}

public class Solution {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(System.in));

        String tipoVehiculo = bufferedReader.readLine();
        String fechaEntrada = bufferedReader.readLine();
        String horaEntrada = bufferedReader.readLine();
        String fechaSalida = bufferedReader.readLine();
        String horaSalida = bufferedReader.readLine();

        String result = Result.calcularEstancia(
            tipoVehiculo,
            fechaEntrada,
            horaEntrada,
            fechaSalida,
            horaSalida
        );

        System.out.println(result);
        bufferedReader.close();
    }
}
