import java.util.UUID;
import java.util.Random;

public class Robot {

    private final UUID id;
    private String nombre;
    private String categoria;
    private double peso;
    private double bateria;
    private Movimiento movimiento;

    private static int totalRobots = 0;

    public Robot(String nombre, String categoria, double peso,
                 double bateria, double velocidadMaxima) {

        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.categoria = categoria;
        this.peso = peso;
        this.bateria = bateria;
        this.movimiento = new Movimiento(velocidadMaxima);

        totalRobots++;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPeso() {
        return peso;
    }

    public double getBateria() {
        return bateria;
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public static int getTotalRobots() {
        return totalRobots;
    }

    public void cambiarVelocidad(double nuevaVelocidad) {
        movimiento.cambiarVelocidad(nuevaVelocidad);
    }

    public void avanzar(double tiempo) {
        movimiento.avanzar(tiempo);
    }

    public void consumirBateria(double cantidad) {

        if (cantidad > 0) {

            bateria -= cantidad;

            if (bateria < 0) {
                bateria = 0;
            }
        }
    }

    public boolean estaActivo() {
        return bateria > 0;
    }

    public void simularTurno(Random random) {

        double nuevaVelocidad =
                random.nextDouble() * movimiento.getVelocidadMaxima();

        double tiempo =
                1.0 + random.nextDouble() * 4.0;

        double consumoBateria =
                5.0 + random.nextDouble() * 10.0;

        cambiarVelocidad(nuevaVelocidad);
        avanzar(tiempo);
        consumirBateria(consumoBateria);
    }

    @Override
    public String toString() {

        return String.format(
                "Robot{id=%s, nombre='%s', categoria='%s', peso=%.2f, bateria=%.2f, velocidadActual=%.2f, distanciaRecorrida=%.2f}",
                id,
                nombre,
                categoria,
                peso,
                bateria,
                movimiento.getVelocidadActual(),
                movimiento.getDistanciaRecorrida()
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Robot otroRobot = (Robot) obj;

        return id.equals(otroRobot.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}