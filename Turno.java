public class Turno {
    private int id;
    private String cliente;
    private String telefono;
    private String barbero;
    private String fecha;
    private String horario;
    private String estado;

    // Constructor completo
    public Turno(int id, String cliente, String telefono, String barbero, String fecha, String horario, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.telefono = telefono;
        this.barbero = barbero;
        this.fecha = fecha;
        this.horario = horario;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getBarbero() {
        return barbero;
    }

    public void setBarbero(String barbero) {
        this.barbero = barbero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}