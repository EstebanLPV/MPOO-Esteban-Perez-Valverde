import java.util.Random;

public class BattleBot {

    public static void main(String[] args) {

        Random random = new Random();

        Robot robot1 = new Robot(
                "Titan",
                "Pesado",
                18.5,
                100.0,
                15.0
        );

        Robot robot2 = new Robot(
                "Raptor",
                "Mediano",
                14.2,
                100.0,
                18.0
        );

        Robot robot3 = new Robot(
                "Bolt",
                "Ligero",
                10.8,
                100.0,
                22.0
        );

        System.out.println(
                "Robots participantes: " + Robot.getTotalRobots()
        );

        System.out.println("\n--- Estado inicial ---");

        System.out.println(robot1);
        System.out.println(robot2);
        System.out.println(robot3);

        robot1.simularTurno(random);
        robot2.simularTurno(random);
        robot3.simularTurno(random);

        System.out.println("\n--- Estado después de la batalla ---");

        System.out.println(robot1);
        System.out.println(robot2);
        System.out.println(robot3);

        System.out.println("\n--- Prueba de referencias ---");

        Robot robotFavorito = robot1;

        robot1.consumirBateria(10);

        System.out.println("robot1:");
        System.out.println(robot1);

        System.out.println("robotFavorito:");
        System.out.println(robotFavorito);

        Robot robotCopia = new Robot(
                "Titan",
                "Pesado",
                18.5,
                100.0,
                15.0
        );

        System.out.println("\n--- Prueba de igualdad ---");

        System.out.println(
                "robot1.equals(robotCopia): "
                        + robot1.equals(robotCopia)
        );

        System.out.println(
                "robot1.equals(robotFavorito): "
                        + robot1.equals(robotFavorito)
        );
    }
}