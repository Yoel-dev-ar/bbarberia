public class Turno {
    private int id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String servicio;
    private String barberoAsignado; // Asignado automáticamente por disponibilidad
    private String hora;
    private String estado; // "PENDIENTE", "PRESENTE", "AUSENTE"

    public Turno(int id, String nombre, String apellido, String telefono, String servicio, String barberoAsignado, String hora) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.servicio = servicio;
        this.barberoAsignado = barberoAsignado;
        this.hora = hora;
        this.estado = "PENDIENTE"; // Nace pendiente por defecto
    }

    // Getters y Setters básicos
    public int getId() { return id; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) {
      this.estado = estado;
    }
    public String getHora() { return hora; }
    
    public void mostrarDatos() {
        System.out.println("ID: " + id + 
                           " | Cliente: " + nombre + " " + apellido + 
                           " | Tel: " + telefono + 
                           " | Servicio: " + servicio + 
                           " | Barbero Asignado: " + barberoAsignado + 
                           " | Hora: " + hora + 
                           " | Estado: " + estado);
    }
}