public class Movimiento {

    private double velocidadMaxima;
    private double velocidadActual;
    private double distanciaRecorrida;

    public Movimiento(double velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
        this.velocidadActual = 0.0;
        this.distanciaRecorrida = 0.0;
    }

    public double getVelocidadMaxima() {
        return velocidadMaxima;
    }

    public double getVelocidadActual() {
        return velocidadActual;
    }

    public double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public void cambiarVelocidad(double nuevaVelocidad) {
        if (nuevaVelocidad >= 0 && nuevaVelocidad <= velocidadMaxima) {
            this.velocidadActual = nuevaVelocidad;
        }
    }

    public void avanzar(double tiempo) {
        if (tiempo > 0) {
            this.distanciaRecorrida += velocidadActual * tiempo;
        }
    }
}