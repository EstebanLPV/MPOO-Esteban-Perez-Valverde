
import java.util.Scanner;
public class SistemaBancario {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CuentaBancariaService servicio =
                new CuentaBancariaService();
        int opcion;
        do {
            System.out.println();
            System.out.println("========== SISTEMA BANCARIO ==========");
            System.out.println();
            System.out.println("1. Transferir dinero");
            System.out.println("2. Depositar dinero");
            System.out.println("3. Retirar dinero");
            System.out.println("4. Salir");
            System.out.println();
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();
            switch (opcion) {
                case 1:
                    System.out.print("Cuenta origen: ");
                    String origen = scanner.nextLine();
                    System.out.print("Cuenta destino: ");
                    String destino = scanner.nextLine();
                    System.out.print("Cantidad: ");
                    double cantidadTransferencia = scanner.nextDouble();
                    boolean transferencia =
                            servicio.transferir(
                                    origen,
                                    destino,
                                    cantidadTransferencia
                            );
                    if (transferencia) {
                        System.out.println("Transferencia realizada correctamente.");
                    } else {
                        System.out.println("No se pudo realizar la transferencia.");
                    }
                    break;
                case 2:
                    System.out.print("Cuenta destino: ");
                    String cuentaDeposito = scanner.nextLine();
                    System.out.print("Cantidad: ");
                    double cantidadDeposito = scanner.nextDouble();
                    boolean deposito =
                            servicio.depositar(
                                    cuentaDeposito,
                                    cantidadDeposito
                            );
                    if (deposito) {
                        System.out.println("Deposito realizado correctamente.");
                    } else {
                        System.out.println("No se pudo realizar el deposito.");
                    }
                    break;
                case 3:
                    System.out.print("Cuenta origen: ");
                    String cuentaRetiro = scanner.nextLine();

                    System.out.print("Cantidad: ");
                    double cantidadRetiro = scanner.nextDouble();
                    boolean retiro =
                            servicio.retirar(
                                    cuentaRetiro,
                                    cantidadRetiro
                            );
                    if (retiro) {
                        System.out.println("Retiro realizado correctamente.");
                    } else {
                        System.out.println("No se pudo realizar el retiro.");
                    }
                    break;
                case 4:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        } while (opcion != 4);
        scanner.close();
    }
}