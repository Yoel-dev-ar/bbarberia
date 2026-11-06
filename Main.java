import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestionBarberia barberia = new GestionBarberia();

        System.out.println("=========================================");
        System.out.println("  SISTEMA DE GESTIÓN DE BARBERÍA (SPRINT 1)");
        System.out.println("=========================================");

        // 1. LOGIN INTERACTIVO
        boolean accesoConcedido = false;
        while (!accesoConcedido) {
            System.out.print("\nIngrese usuario (admin / empleado): ");
            String usuario = scanner.nextLine();
            
            System.out.print("Ingrese contraseña (1234): ");
            String password = scanner.nextLine();

            accesoConcedido = barberia.validarLogin(usuario, password);
            if (!accesoConcedido) {
                System.out.println("Credenciales incorrectas. Intente nuevamente.");
            }
        }

        // 2. MENÚ PRINCIPAL
        int opcion = 0;
        while (opcion != 4) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Registrar nuevo turno");
            System.out.println("2. Ver agenda del día");
            System.out.println("3. Cambiar estado de asistencia (Presente / Ausente)");
            System.out.println("4. Salir");
            System.out.print("Elija una opción: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer de entrada
            } else {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("\n--- REGISTRO DE TURNO ---");
                    System.out.print("Nombre del cliente: ");
                    String nombre = scanner.nextLine();
                    
                    System.out.print("Apellido del cliente: ");
                    String apellido = scanner.nextLine();
                    
                    System.out.print("Teléfono del cliente: ");
                    String telefono = scanner.nextLine();
                    
                    System.out.print("Servicio (ej: Corte, Barba): ");
                    String servicio = scanner.nextLine();
                    
                    System.out.print("Hora del turno (ej: 15:00): ");
                    String hora = scanner.nextLine();

                    // Registra el turno y asigna el barbero automáticamente
                    barberia.registrarTurno(nombre, apellido, telefono, servicio, hora);
                    break;

                case 2:
                    barberia.verAgenda();
                    break;

                case 3:
                    System.out.println("\n--- CONTROL DE ASISTENCIA ---");
                    barberia.verAgenda();
                    System.out.print("\nIngrese el ID del turno a modificar: ");
                    if (scanner.hasNextInt()) {
                        int idTurno = scanner.nextInt();
                        scanner.nextLine(); // Limpiar buffer
                        
                        System.out.print("Ingrese el nuevo estado (PRESENTE / AUSENTE): ");
                        String nuevoEstado = scanner.nextLine().toUpperCase();
                        
                        barberia.cambiarAsistencia(idTurno, nuevoEstado);
                    } else {
                        System.out.println("ID inválido.");
                        scanner.nextLine();
                    }
                    break;

                case 4:
                    System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    break;

                default:
                    System.out.println("Opción no válida. Elija entre 1 y 4.");
            }
        }
        scanner.close();
    }
}