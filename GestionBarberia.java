import java.util.ArrayList;
import java.util.List;

public class GestionBarberia {
    private List<Turno> listaTurnos = new ArrayList<>();
    private int contadorId = 1;

    // 1. Tarjeta: Login de ingreso (administrador y empleados)
    public boolean validarLogin(String usuario, String password) {
        if ((usuario.equals("admin") || usuario.equals("empleado")) && password.equals("1234")) {
            System.out.println("¡Ingreso exitoso!");
            return true;
        }
        System.out.println("Usuario o contraseña incorrectos.");
        return false;
    }

    // Método auxiliar interno para asignar un barbero según disponibilidad (simulado para el MVP)
    private String asignarBarberoAutomatico(String hora, String servicio) {
        // Lógica simple: cuenta cuántos turnos hay a esa hora y asigna el disponible
        long ocupados = listaTurnos.stream().filter(t -> t.getHora().equals(hora)).count();
        if (ocupados % 2 == 0) {
            return "Barbero A";
        } else {
            return "Barbero B";
        }
    }

    // 2. Tarjeta: Ingreso de datos del cliente (Solo Nombre, Apellido y Teléfono)
    public void registrarTurno(String nombre, String apellido, String telefono, String servicio, String hora) {
        // El sistema asigna el barbero automáticamente de acuerdo a la disponibilidad
        String barberoAsignado = asignarBarberoAutomatico(hora, servicio);

        Turno nuevoTurno = new Turno(contadorId++, nombre, apellido, telefono, servicio, barberoAsignado, hora);
        listaTurnos.add(nuevoTurno);
        System.out.println("Turno registrado con éxito. Asignado a: " + barberoAsignado);
    }

    // 3. Tarjeta: Visualización de la Agenda
    public void verAgenda() {
        System.out.println("\n--- AGENDA DE TURNOS DEL DÍA ---");
        for (Turno t : listaTurnos) {
            t.mostrarDatos();
        }
    }

    // 4. Tarjeta: Control de asistencia
    public void cambiarAsistencia(int idTurno, String nuevoEstado) {
        for (Turno t : listaTurnos) {
            if (t.getId() == idTurno) {
                t.setEstado(nuevoEstado); // Cambia a "PRESENTE" o "AUSENTE"
                System.out.println("Estado actualizado a: " + nuevoEstado);
                return;
            }
        }
        System.out.println("Turno no encontrado.");
    }
}