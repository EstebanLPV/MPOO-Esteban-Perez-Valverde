public class CuentaBancariaService {
    private CuentaBancaria[] cuentas;
    public CuentaBancariaService() {
        cuentas = new CuentaBancaria[3];
        cuentas[0] = new CuentaBancaria(
                "1001",
                "Luis",
                5000,
                true
        );
        cuentas[1] = new CuentaBancaria(
                "1002",
                "Ana",
                3000,
                true
        );
        cuentas[2] = new CuentaBancaria(
                "1003",
                "Carlos",
                1500,
                true
        );
    }
    private CuentaBancaria buscarCuenta(String numeroCuenta) {
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta.getNumeroCuenta().equals(numeroCuenta)) {
                return cuenta;
            }
        }
        return null;
    }
    public boolean depositar(String cuentaDestino, double cantidad) {
        CuentaBancaria cuenta = buscarCuenta(cuentaDestino);
        if (cuenta == null || cantidad <= 0) {
            return false;
        }
        return cuenta.depositar(cantidad);
    }
    public boolean retirar(String cuentaOrigen, double cantidad) {
        CuentaBancaria cuenta = buscarCuenta(cuentaOrigen);

        if (cuenta == null || cantidad <= 0) {
            return false;
        }
        return cuenta.retirar(cantidad);
    }
    public boolean transferir(String cuentaOrigen,
                              String cuentaDestino,
                              double cantidad) {

        CuentaBancaria origen = buscarCuenta(cuentaOrigen);
        CuentaBancaria destino = buscarCuenta(cuentaDestino);
        if (origen == null || destino == null) {
            return false;
        }
        if (cuentaOrigen.equals(cuentaDestino)) {
            return false;
        }
        if (cantidad <= 0) {
            return false;
        }
        if (!origen.isActiva() || !destino.isActiva()) {
            return false;
        }
        if (cantidad > origen.getSaldo()) {
            return false;
        }
        origen.retirar(cantidad);
        destino.depositar(cantidad);
        return true;
    }
}