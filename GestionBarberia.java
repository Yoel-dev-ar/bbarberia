import java.util.ArrayList;
import java.util.List;

public class GestionBarberia {
    private List<Turno> listaTurnos = new ArrayList<>();
    private int contadorId = 1;

    // Validación de login compatible
    public boolean validarLogin(String usuario, String password) {
        return (usuario.equalsIgnoreCase("admin") || 
                usuario.equalsIgnoreCase("Carlos") || 
                usuario.equalsIgnoreCase("Lucas")) && password.equals("1234");
    }

    // Método para ver la agenda interna
    public List<Turno> verAgenda() {
        return listaTurnos;
    }

    // Método para cambiar asistencia / estado
    public void cambiarAsistencia(int id, String nuevoEstado) {
        for (Turno t : listaTurnos) {
            if (t.getId() == id) {
                t.setEstado(nuevoEstado);
            }
        }
    }

    // Método para asignar un barbero según disponibilidad
    private String asignarBarberoAutomatico(String horario, String servicio) {
        long ocupados = listaTurnos.stream().filter(t -> t.getHorario().equals(horario)).count();
        if (ocupados % 2 == 0) {
            return "Carlos";
        } else {
            return "Lucas";
        }
    }

    // Método para registrar un turno
    public void registrarTurno(String cliente, String telefono, String barbero, String fecha, String horario) {
        String barberoAsignado = (barbero == null || barbero.isEmpty()) ? asignarBarberoAutomatico(horario, "Corte") : barbero;

        Turno nuevoTurno = new Turno(contadorId++, cliente, telefono, barberoAsignado, fecha, horario, "Pendiente");
        listaTurnos.add(nuevoTurno);
        System.out.println("Turno registrado con éxito. Asignado a: " + barberoAsignado);
    }
}