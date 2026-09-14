import java.io.*;
import java.util.*;

class Result {

    static class PilaDinamica {

        private double[] elementos;
        private int cima;

        public PilaDinamica(int capacidadInicial) {
            elementos = new double[capacidadInicial];
            cima = 0;
        }

        public void push(double valor) {
            if (cima == elementos.length) {
                ampliarCapacidad();
            }

            elementos[cima] = valor;
            cima++;
        }

        public double pop() {
            if (isEmpty()) {
                return Double.NaN;
            }

            cima--;

            double valorEliminado = elementos[cima];
            elementos[cima] = 0.0;

            return valorEliminado;
        }

        public double peek() {
            if (isEmpty()) {
                return Double.NaN;
            }

            return elementos[cima - 1];
        }

        public boolean isEmpty() {
            return cima == 0;
        }

        public int size() {
            return cima;
        }

        public int capacity() {
            return elementos.length;
        }

        private void ampliarCapacidad() {
            int nuevaCapacidad = elementos.length * 2;

            double[] nuevoArreglo =
                    new double[nuevaCapacidad];

            for (int i = 0; i < elementos.length; i++) {
                nuevoArreglo[i] = elementos[i];
            }

            elementos = nuevoArreglo;
        }
    }

    /*
     * NO MODIFICAR. Reservado para HackerRank
     */
    public static List<String> procesarOperaciones(
            int capacidad, List<String> operaciones) {

        PilaDinamica pila = new PilaDinamica(capacidad);

        List<String> salida = new ArrayList<>();

        for (String linea : operaciones) {

            String[] partes = linea.trim().split("\\s+");

            String operacion = partes[0];

            switch (operacion) {

                case "PUSH":

                    double valor =
                            Double.parseDouble(partes[1]);

                    pila.push(valor);

                    salida.add("OK");

                    break;

                case "POP":

                    double eliminado = pila.pop();

                    if (Double.isNaN(eliminado)) {
                        salida.add("EMPTY");
                    } else {
                        salida.add(
                                String.valueOf(eliminado)
                        );
                    }

                    break;

                case "PEEK":

                    double cima = pila.peek();

                    if (Double.isNaN(cima)) {
                        salida.add("EMPTY");
                    } else {
                        salida.add(
                                String.valueOf(cima)
                        );
                    }

                    break;

                case "SIZE":

                    salida.add(
                            String.valueOf(
                                    pila.size()
                            )
                    );

                    break;

                case "CAPACITY":

                    salida.add(
                            String.valueOf(
                                    pila.capacity()
                            )
                    );

                    break;

                case "ISEMPTY":

                    salida.add(
                            String.valueOf(
                                    pila.isEmpty()
                            )
                    );

                    break;
            }
        }

        return salida;
    }
}

public class Solution {

    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(System.in)
                );

        BufferedWriter bufferedWriter =
                new BufferedWriter(
                        new FileWriter(
                                System.getenv("OUTPUT_PATH")
                        )
                );

        int capacidad =
                Integer.parseInt(
                        bufferedReader.readLine().trim()
                );

        int nOperaciones =
                Integer.parseInt(
                        bufferedReader.readLine().trim()
                );

        List<String> operaciones = new ArrayList<>();

        for (int i = 0; i < nOperaciones; i++) {
            String operacionesItem =
                    bufferedReader.readLine();

            operaciones.add(operacionesItem);
        }

        List<String> result =
                Result.procesarOperaciones(
                        capacidad,
                        operaciones
                );

        for (int i = 0; i < result.size(); i++) {

            bufferedWriter.write(result.get(i));

            if (i != result.size() - 1) {
                bufferedWriter.write("\n");
            }
        }

        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
