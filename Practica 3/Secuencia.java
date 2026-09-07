public static String comprimir(String datos) {

    String resultado = "";
    int contador = 1;

    for (int i = 1; i < datos.length(); i++) {

        if (datos.charAt(i) == datos.charAt(i - 1) && contador < 9) {
            contador++;
        } else {
            resultado += contador;
            resultado += datos.charAt(i - 1);

            contador = 1;
        }
    }

    resultado += contador;
    resultado += datos.charAt(datos.length() - 1);

    return resultado;
}