package Ejercicio3;
public class CuentaBancaria {
    private String numeroCuenta;
    private String titular;
    private double saldo;
    private boolean activa;
    public CuentaBancaria(String numeroCuenta, String titular,
                          double saldo, boolean activa) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldo;
        this.activa = activa;
    }
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    public String getTitular() {
        return titular;
    }
    public double getSaldo() {
        return saldo;
    }
    public boolean isActiva() {
        return activa;
    }
    public boolean depositar(double cantidad) {
        if (!activa || cantidad <= 0) {
            return false;
        }
        saldo += cantidad;
        return true;
    }
    public boolean retirar(double cantidad) {
        if (!activa || cantidad <= 0 || cantidad > saldo) {
            return false;
        }
        saldo -= cantidad;
        return true;
    }
}