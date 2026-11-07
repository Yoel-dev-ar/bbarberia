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
    private JComboBox<String> comboServicio;
    private JTextField txtFecha;
    private JTextField txtHorario;
    private JTextField txtFiltroFecha;
    private JButton btnFiltrar;
    private JButton btnVerTodos;
    private JButton btnGuardar;
    private JButton btnCerrarSesion;
    private JButton btnCambiarEstado;
    private JButton btnGanancias; // Botón para la ventana emergente de ganancias
    private TurnoDAO turnoDAO;
    private String usuarioLogueado;
    private JPanel panelFormulario;

    public VentanaPrincipal(String usuario) {
        this.usuarioLogueado = usuario;
        turnoDAO = new TurnoDAO();
        setTitle("Sistema de Gestión - Barbería | Usuario: " + usuarioLogueado);
        setSize(1000, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior para el Administrador (11 filas para acomodar el botón de ganancias)
        panelFormulario = new JPanel(new GridLayout(11, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Panel de Administración de Turnos - Sprint 4"));

        panelFormulario.add(new JLabel("Cliente:"));
        txtCliente = new JTextField();
        panelFormulario.add(txtCliente);

        panelFormulario.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelFormulario.add(txtTelefono);

        panelFormulario.add(new JLabel("Barbero:"));
        comboBarbero = new JComboBox<>(new String[]{"Carlos", "Lucas"});
        panelFormulario.add(comboBarbero);

        panelFormulario.add(new JLabel("Servicio:"));
        comboServicio = new JComboBox<>(new String[]{
            "Corte Clásico ($15.000)", 
            "Perfilado de Barba ($8.000)", 
            "Corte + Barba ($20.000)", 
            "Servicio Completo ($25.000)"
        });
        panelFormulario.add(comboServicio);

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

        btnGanancias = new JButton("Calcular Ganancias del Día");
        panelFormulario.add(btnGanancias);

        // Panel superior auxiliar para cerrar sesión
        JPanel panelSuperiorExtra = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrarSesion = new JButton("Cerrar Sesión");
        panelSuperiorExtra.add(btnCerrarSesion);
        
        JPanel norteCompleto = new JPanel(new BorderLayout());
        norteCompleto.add(panelFormulario, BorderLayout.CENTER);
        norteCompleto.add(panelSuperiorExtra, BorderLayout.SOUTH);
        add(norteCompleto, BorderLayout.NORTH);

        // Tabla con 9 columnas en el Centro
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Cliente", "Teléfono", "Barbero", "Fecha", "Horario", "Estado", "Servicio", "Costo"}, 0);
        tablaTurnos = new JTable(modeloTabla);
        
        // --- RENDERER PARA COLOREAR ESTADOS EN LA TABLA ---
        tablaTurnos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                    return c;
                }

                Object estadoObj = table.getModel().getValueAt(row, 6);
                String estado = estadoObj != null ? estadoObj.toString() : "";

                switch (estado.toLowerCase()) {
                    case "atendido":
                        c.setBackground(new Color(200, 230, 201)); // Verde suave
                        c.setForeground(Color.BLACK);
                        break;
                    case "pendiente":
                        c.setBackground(new Color(255, 243, 224)); // Amarillo suave
                        c.setForeground(Color.BLACK);
                        break;
                    case "cancelado":
                    case "ausente":
                        c.setBackground(new Color(255, 205, 210)); // Rojo suave
                        c.setForeground(Color.BLACK);
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                        break;
                }
                return c;
            }
        });

        add(new JScrollPane(tablaTurnos), BorderLayout.CENTER);

        // Panel inferior exclusivo para cambiar estado
        JPanel panelInferior = new JPanel();
        btnCambiarEstado = new JButton("Cambiar Estado del Turno");
        panelInferior.add(btnCambiarEstado);
        add(panelInferior, BorderLayout.SOUTH);

        configurarPermisosRol();
        cargarTurnosIniciales();

        // --- NAVEGACIÓN CON TECLADO (ENTER) ---
        txtCliente.addActionListener(e -> txtTelefono.requestFocus());
        txtTelefono.addActionListener(e -> comboBarbero.requestFocus());
        
        comboBarbero.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    comboServicio.requestFocus();
                }
            }
        });

        comboServicio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtFecha.requestFocus();
                }
            }
        });
        
        txtFecha.addActionListener(e -> txtHorario.requestFocus());
        txtHorario.addActionListener(e -> btnGuardar.doClick());

        // Acciones de los botones
        btnGuardar.addActionListener(e -> guardarTurnoBD());
        btnFiltrar.addActionListener(e -> cargarTurnosPorFiltroFecha());
        btnVerTodos.addActionListener(e -> cargarTodosLosTurnosAdmin());
        btnGanancias.addActionListener(e -> mostrarReporteGananciasDetalladoVentana());
        btnCerrarSesion.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });
        btnCambiarEstado.addActionListener(e -> cambiarEstadoTurno());
    }

    private void configurarPermisosRol() {
        if (!usuarioLogueado.equalsIgnoreCase("admin")) {
            panelFormulario.setVisible(false);
        }
    }

    private void cargarTurnosIniciales() {
        modeloTabla.setRowCount(0);
        List<String[]> listaTurnos;

        if (usuarioLogueado.equalsIgnoreCase("admin")) {
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            listaTurnos = turnoDAO.obtenerTurnosPorFecha(fechaHoy);
            txtFiltroFecha.setText(fechaHoy);
        } else {
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
        String servicioSeleccionado = (String) comboServicio.getSelectedItem();
        String fecha = txtFecha.getText().trim();
        String horario = txtHorario.getText().trim();
        String estado = "Pendiente";

        if (cliente.isEmpty() || telefono.isEmpty() || fecha.isEmpty() || horario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double costo = 0.0;
        if (servicioSeleccionado.contains("15.000")) {
            costo = 15000.0;
        } else if (servicioSeleccionado.contains("8.000")) {
            costo = 8000.0;
        } else if (servicioSeleccionado.contains("20.000")) {
            costo = 20000.0;
        } else if (servicioSeleccionado.contains("25.000")) {
            costo = 25000.0;
        }

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

        boolean exito = turnoDAO.guardarTurno(cliente, telefono, barbero, fecha, horario, estado, servicioSeleccionado, costo);
        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Turno guardado con éxito!");
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
            String[] estados = usuarioLogueado.equalsIgnoreCase("admin") 
                ? new String[]{"Pendiente", "Atendido", "Ausente", "Cancelado"} 
                : new String[]{"Pendiente", "Atendido"};
            
            String nuevoEstado = (String) JOptionPane.showInputDialog(
                this, "Seleccione el nuevo estado del turno:", "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE, null, estados, estados[0]
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

    private void mostrarReporteGananciasDetalladoVentana() {
        String fechaConsulta = txtFiltroFecha.getText().trim();
        if (fechaConsulta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese o filtre una fecha para calcular las ganancias.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> turnosDelDia = turnoDAO.obtenerTurnosPorFecha(fechaConsulta);

        StringBuilder seccionCarlos = new StringBuilder();
        StringBuilder seccionLucas = new StringBuilder();

        double totalCarlos = 0.0;
        double totalLucas = 0.0;

        seccionCarlos.append("✂️ BARBERO: CARLOS\n------------------------------------------------------------\n");
        seccionLucas.append("✂️ BARBERO: LUCAS\n------------------------------------------------------------\n");

        int countCarlos = 0;
        int countLucas = 0;

        for (String[] t : turnosDelDia) {
            String cliente = t[1];
            String barbero = t[3];
            String horario = t[5];
            String estado = t[6];
            String servicio = t[7];
            String costoStr = t[8];

            double costoNum = 0.0;
            if (estado.equalsIgnoreCase("atendido")) {
                try {
                    String limpio = costoStr.replace("$", "").replace(".0", "").trim();
                    costoNum = Double.parseDouble(limpio);
                } catch (Exception ignored) {}
            }

            String lineaDetalle = "• Hora: " + horario + " | Cliente: " + cliente + 
                                  "\n  Estado: " + estado + " | Servicio: " + servicio + 
                                  " | Costo: " + costoStr + "\n\n";

            if (barbero.equalsIgnoreCase("carlos")) {
                seccionCarlos.append(lineaDetalle);
                countCarlos++;
                if (estado.equalsIgnoreCase("atendido")) {
                    totalCarlos += costoNum;
                }
            } else if (barbero.equalsIgnoreCase("lucas")) {
                seccionLucas.append(lineaDetalle);
                countLucas++;
                if (estado.equalsIgnoreCase("atendido")) {
                    totalLucas += costoNum;
                }
            }
        }

        if (countCarlos == 0) seccionCarlos.append("No hay turnos registrados para este barbero.\n");
        seccionCarlos.append("💵 Recaudación Neta (Atendidos): $").append(totalCarlos).append("\n\n");

        if (countLucas == 0) seccionLucas.append("No hay turnos registrados para este barbero.\n");
        seccionLucas.append("💵 Recaudación Neta (Atendidos): $").append(totalLucas).append("\n\n");

        double totalGeneral = totalCarlos + totalLucas;

        StringBuilder reporteFinal = new StringBuilder();
        reporteFinal.append("📊 REPORTE FINANCIERO Y DETALLADO DEL DÍA: ").append(fechaConsulta).append("\n");
        reporteFinal.append("============================================================\n\n");
        reporteFinal.append(seccionCarlos.toString());
        reporteFinal.append("============================================================\n");
        reporteFinal.append(seccionLucas.toString());
        reporteFinal.append("============================================================\n");
        reporteFinal.append("💰 RECAUDACIÓN TOTAL GENERAL (Ambos Barberos): $").append(totalGeneral);

        JTextArea textArea = new JTextArea(reporteFinal.toString(), 22, 55);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane, "Reporte Detallado por Barbero", JOptionPane.INFORMATION_MESSAGE);
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