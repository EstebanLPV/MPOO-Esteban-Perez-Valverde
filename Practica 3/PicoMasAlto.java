import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'longestPeak' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY array as parameter.
     */

    public static int longestPeak(int[] array) {

        int maxLength = 0;

        for (int i = 1; i < array.length - 1; i++) {

            // Revisamos si el elemento actual puede ser la cima
            boolean isPeak = array[i] > array[i - 1] + 1
                          && array[i] > array[i + 1];

            if (isPeak) {

                int left = i - 1;
                int right = i + 1;

                // Buscar hasta dónde comienza la subida
                while (left > 0 &&
                       array[left] > array[left - 1] + 1) {

                    left--;
                }

                // Buscar hasta dónde termina la bajada
                while (right < array.length - 1 &&
                       array[right] > array[right + 1]) {

                    right++;
                }

                // Calcular longitud del pico
                int length = right - left + 1;

                if (length > maxLength) {
                    maxLength = length;
                }
            }
        }

        return maxLength;
    }
}

public class Solution {

    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(System.in));

        BufferedWriter bufferedWriter =
                new BufferedWriter(
                        new FileWriter(System.getenv("OUTPUT_PATH"))
                );

        String line = bufferedReader.readLine();

        String[] values = line.trim().split("\\s+");

        int[] array = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            array[i] = Integer.parseInt(values[i]);
        }

        int result = Result.longestPeak(array);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
