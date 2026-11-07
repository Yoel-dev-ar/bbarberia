import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JTable tablaTurnos;
    private DefaultTableModel modeloTabla;
    private JTextField txtCliente;
    private JTextField txtTelefono;
    private JComboBox<String> comboBarbero;
    private JTextField txtFecha;
    private JTextField txtHorario;
    private JTextField txtFiltroFecha;
    private JButton btnFiltrar;
    private JButton btnVerTodos;
    private JButton btnGuardar;
    private JButton btnCerrarSesion;
    private JButton btnCambiarEstado;
    private TurnoDAO turnoDAO;
    private String usuarioLogueado;
    private JPanel panelFormulario;

    public VentanaPrincipal(String usuario) {
        this.usuarioLogueado = usuario;
        turnoDAO = new TurnoDAO();
        setTitle("Sistema de Gestión - Barbería | Usuario: " + usuarioLogueado);
        setSize(850, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior para el Administrador
        panelFormulario = new JPanel(new GridLayout(9, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Panel de Administración de Turnos"));

        panelFormulario.add(new JLabel("Cliente:"));
        txtCliente = new JTextField();
        panelFormulario.add(txtCliente);

        panelFormulario.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelFormulario.add(txtTelefono);

        panelFormulario.add(new JLabel("Barbero:"));
        comboBarbero = new JComboBox<>(new String[]{"Carlos", "Lucas"});
        panelFormulario.add(comboBarbero);

        panelFormulario.add(new JLabel("Fecha del Turno (ej. 15/11/2026):"));
        txtFecha = new JTextField();
        panelFormulario.add(txtFecha);

        panelFormulario.add(new JLabel("Horario (ej. 10:00):"));
        txtHorario = new JTextField();
        panelFormulario.add(txtHorario);

        btnGuardar = new JButton("Guardar Turno");
        panelFormulario.add(btnGuardar);

        panelFormulario.add(new JLabel("Filtrar Tabla por Fecha:"));
        txtFiltroFecha = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        panelFormulario.add(txtFiltroFecha);

        btnFiltrar = new JButton("Filtrar Fecha");
        panelFormulario.add(btnFiltrar);

        btnVerTodos = new JButton("Ver Todos los Turnos");
        panelFormulario.add(btnVerTodos);

        // Panel de sesión auxiliar
        JPanel panelSuperiorExtra = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrarSesion = new JButton("Cerrar Sesión");
        panelSuperiorExtra.add(btnCerrarSesion);
        
        JPanel norteCompleto = new JPanel(new BorderLayout());
        norteCompleto.add(panelFormulario, BorderLayout.CENTER);
        norteCompleto.add(panelSuperiorExtra, BorderLayout.SOUTH);
        add(norteCompleto, BorderLayout.NORTH);

        // Tabla para visualizar los turnos
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Cliente", "Teléfono", "Barbero", "Fecha", "Horario", "Estado"}, 0);
        tablaTurnos = new JTable(modeloTabla);
        add(new JScrollPane(tablaTurnos), BorderLayout.CENTER);

        // Panel inferior para cambiar estado
        JPanel panelInferior = new JPanel();
        btnCambiarEstado = new JButton("Cambiar Estado del Turno");
        panelInferior.add(btnCambiarEstado);
        add(panelInferior, BorderLayout.SOUTH);

        configurarPermisosRol();
        cargarTurnosIniciales();

        // --- NAVEGACIÓN CON TECLADO ---
        txtCliente.addActionListener(e -> txtTelefono.requestFocus());
        txtTelefono.addActionListener(e -> comboBarbero.requestFocus());
        
        comboBarbero.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtFecha.requestFocus();
                }
            }
        });
        
        txtFecha.addActionListener(e -> txtHorario.requestFocus());
        txtHorario.addActionListener(e -> btnGuardar.doClick());

        // Acciones de botones
        btnGuardar.addActionListener(e -> guardarTurnoBD());
        btnFiltrar.addActionListener(e -> cargarTurnosPorFiltroFecha());
        btnVerTodos.addActionListener(e -> cargarTodosLosTurnosAdmin());
        btnCerrarSesion.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });
        btnCambiarEstado.addActionListener(e -> cambiarEstadoTurno());
    }

    private void configurarPermisosRol() {
        if (!usuarioLogueado.equalsIgnoreCase("admin")) {
            panelFormulario.setVisible(false); // Los barberos no ven el panel de administración
        }
    }

    private void cargarTurnosIniciales() {
        modeloTabla.setRowCount(0);
        List<String[]> listaTurnos;

        if (usuarioLogueado.equalsIgnoreCase("admin")) {
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            listaTurnos = turnoDAO.obtenerTurnosPorFecha(fechaHoy);
        } else {
            // Los barberos visualizan sus turnos asignados ordenados cronológicamente
            listaTurnos = turnoDAO.obtenerTurnosPorBarbero(usuarioLogueado);
        }

        for (String[] turno : listaTurnos) {
            modeloTabla.addRow(turno);
        }
    }

    private void cargarTurnosPorFiltroFecha() {
        String fechaBuscada = txtFiltroFecha.getText().trim();
        if (fechaBuscada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una fecha para filtrar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0);
        List<String[]> lista = turnoDAO.obtenerTurnosPorFecha(fechaBuscada);
        for (String[] turno : lista) {
            modeloTabla.addRow(turno);
        }
    }

    private void cargarTodosLosTurnosAdmin() {
        modeloTabla.setRowCount(0);
        List<String[]> lista = turnoDAO.obtenerTodosLosTurnos();
        for (String[] turno : lista) {
            modeloTabla.addRow(turno);
        }
    }

    private void guardarTurnoBD() {
        String cliente = txtCliente.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String barbero = (String) comboBarbero.getSelectedItem();
        String fecha = txtFecha.getText().trim();
        String horario = txtHorario.getText().trim();
        String estado = "Pendiente";

        if (cliente.isEmpty() || telefono.isEmpty() || fecha.isEmpty() || horario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- VALIDACIÓN DE SOLAPAMIENTO ---
        List<String[]> turnosDelBarbero = turnoDAO.obtenerTurnosPorBarbero(barbero);
        for (String[] t : turnosDelBarbero) {
            String fechaExistente = t[4]; 
            String horarioExistente = t[5]; 
            String estadoExistente = t[6];   

            if (fechaExistente != null && !fechaExistente.isEmpty() &&
                fechaExistente.equalsIgnoreCase(fecha) && 
                horarioExistente.equalsIgnoreCase(horario) && 
                !estadoExistente.equalsIgnoreCase("Cancelado")) {
                
                JOptionPane.showMessageDialog(this, 
                    "El barbero " + barbero + " ya tiene un turno asignado el " + fecha + " a las " + horario + ".", 
                    "Horario Ocupado", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        boolean exito = turnoDAO.guardarTurno(cliente, telefono, barbero, fecha, horario, estado);
        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Turno guardado con éxito!");
            
            // Actualiza la tabla mostrando la fecha del turno recién ingresado
            txtFiltroFecha.setText(fecha);
            cargarTurnosPorFiltroFecha();

            txtCliente.setText("");
            txtTelefono.setText("");
            txtFecha.setText("");
            txtHorario.setText("");
            txtCliente.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el turno en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoTurno() {
        int filaSeleccionada = tablaTurnos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un turno de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idTurno = Integer.parseInt(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
            String[] estados;

            if (usuarioLogueado.equalsIgnoreCase("admin")) {
                estados = new String[]{"Pendiente", "Atendido", "Ausente", "Cancelado"};
            } else {
                estados = new String[]{"Pendiente", "Atendido"};
            }
            
            String nuevoEstado = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione el nuevo estado del turno:",
                "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                estados[0]
            );

            if (nuevoEstado != null) {
                boolean actualizado = turnoDAO.actualizarEstado(idTurno, nuevoEstado);
                if (actualizado) {
                    JOptionPane.showMessageDialog(this, "¡Estado actualizado a: " + nuevoEstado + "!");
                    if (usuarioLogueado.equalsIgnoreCase("admin")) {
                        cargarTurnosPorFiltroFecha();
                    } else {
                        cargarTurnosIniciales();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar el turno: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public VentanaPrincipal() {
        this("admin");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal("admin").setVisible(true);
        });
    }
}