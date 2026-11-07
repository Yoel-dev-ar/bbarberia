import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private JButton btnGanancias; 
    private JButton btnWhatsApp;  
    private JButton btnConsultaRapida; 
    private TurnoDAO turnoDAO;
    private String usuarioLogueado;
    private JPanel panelFormulario;

    public VentanaPrincipal(String usuario) {
        this.usuarioLogueado = usuario;
        turnoDAO = new TurnoDAO();
        setTitle("Sistema de Gestión - Barbería | Usuario: " + usuarioLogueado);
        setSize(1000, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelFormulario = new JPanel(new GridLayout(12, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panelFormulario.add(new JLabel("Cliente (Nombre y Apellido):"));
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
            "Sin definir / A confirmar",
            "Corte Clásico ($15.000)", 
            "Perfilado de Barba ($8.000)", 
            "Corte + Barba ($20.000)", 
            "Servicio Completo ($25.000)"
        });
        panelFormulario.add(comboServicio);

        panelFormulario.add(new JLabel("Fecha de Interés (ej. 15/11/2026):"));
        txtFecha = new JTextField();
        panelFormulario.add(txtFecha);

        panelFormulario.add(new JLabel("Horario (Opcional si agenda):"));
        txtHorario = new JTextField();
        panelFormulario.add(txtHorario);

        btnGuardar = new JButton("Guardar Turno");
        panelFormulario.add(btnGuardar);

        btnConsultaRapida = new JButton("💬 Enviar Horarios Libres (Sin Guardar)");
        panelFormulario.add(btnConsultaRapida);

        btnWhatsApp = new JButton("💬 Opciones WhatsApp (Turno Seleccionado)");
        panelFormulario.add(btnWhatsApp);

        panelFormulario.add(new JLabel("Filtrar Tabla por Fecha:"));
        txtFiltroFecha = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        panelFormulario.add(txtFiltroFecha);

        btnFiltrar = new JButton("Filtrar Fecha");
        panelFormulario.add(btnFiltrar);

        btnVerTodos = new JButton("Ver Todos los Turnos");
        panelFormulario.add(btnVerTodos);

        panelFormulario.add(new JLabel("")); 

        btnGanancias = new JButton("Calcular Ganancias del Día");
        panelFormulario.add(btnGanancias);

        JPanel panelSuperiorExtra = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrarSesion = new JButton("Cerrar Sesión");
        panelSuperiorExtra.add(btnCerrarSesion);
        
        JPanel norteCompleto = new JPanel(new BorderLayout());
        norteCompleto.add(panelFormulario, BorderLayout.CENTER);
        norteCompleto.add(panelSuperiorExtra, BorderLayout.SOUTH);
        add(norteCompleto, BorderLayout.NORTH);

        // Tabla con celdas NO editables directamente (se cambia estado solo por botón)
        modeloTabla = new DefaultTableModel(new String[]{"Nº", "Cliente", "Teléfono", "Barbero", "Fecha", "Horario", "Estado", "Servicio", "Costo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bloquea la edición manual haciendo clic en la celda
            }
        };
        
        tablaTurnos = new JTable(modeloTabla);
        
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
                        c.setBackground(new Color(200, 230, 201)); 
                        c.setForeground(Color.BLACK);
                        break;
                    case "pendiente":
                    case "sin horario":
                        c.setBackground(new Color(255, 243, 224)); 
                        c.setForeground(Color.BLACK);
                        break;
                    case "cancelado":
                    case "ausente":
                        c.setBackground(new Color(255, 205, 210)); 
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

        JPanel panelInferior = new JPanel();
        btnCambiarEstado = new JButton("Cambiar Estado del Turno");
        panelInferior.add(btnCambiarEstado);
        add(panelInferior, BorderLayout.SOUTH);

        configurarPermisosRol();
        cargarTurnosIniciales();

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

        btnGuardar.addActionListener(e -> guardarTurnoBD());
        btnConsultaRapida.addActionListener(e -> enviarWhatsAppSinGuardar());
        btnFiltrar.addActionListener(e -> cargarTurnosPorFiltroFecha());
        btnVerTodos.addActionListener(e -> cargarTodosLosTurnosAdmin());
        btnGanancias.addActionListener(e -> mostrarReporteGananciasDetalladoVentana());
        btnWhatsApp.addActionListener(e -> menuOpcionesWhatsApp());
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

    // --- MÉTODO AUXILIAR PARA CARGAR LA TABLA REINICIANDO LA NUMERACIÓN (1, 2, 3...) POR CADA DÍA ---
    private void cargarTablaOrdenadaPorFecha(List<String[]> listaTurnos) {
        modeloTabla.setRowCount(0);
        String fechaAnterior = "";
        int contadorOrden = 1;

        for (String[] turno : listaTurnos) {
            String fechaActual = turno[4]; // Columna Fecha
            if (!fechaActual.equals(fechaAnterior)) {
                contadorOrden = 1; // Reinicia el contador a 1 cuando cambia de día
                fechaAnterior = fechaActual;
            }

            String[] filaConOrden = turno.clone();
            filaConOrden[0] = String.valueOf(contadorOrden++); // Reemplaza el ID por el número correlativo del día
            modeloTabla.addRow(filaConOrden);
        }
    }

    private void cargarTurnosIniciales() {
        List<String[]> listaTurnos;

        if (usuarioLogueado.equalsIgnoreCase("admin")) {
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            listaTurnos = turnoDAO.obtenerTurnosPorFecha(fechaHoy);
            txtFiltroFecha.setText(fechaHoy);
        } else {
            listaTurnos = turnoDAO.obtenerTurnosPorBarbero(usuarioLogueado).stream()
                .filter(t -> !t[6].equalsIgnoreCase("Cancelado") && !t[6].equalsIgnoreCase("Ausente"))
                .collect(Collectors.toList());
        }

        cargarTablaOrdenadaPorFecha(listaTurnos);
    }

    private void cargarTurnosPorFiltroFecha() {
        String fechaBuscada = txtFiltroFecha.getText().trim();
        if (fechaBuscada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una fecha para filtrar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> lista = turnoDAO.obtenerTurnosPorFecha(fechaBuscada);
        
        if (!usuarioLogueado.equalsIgnoreCase("admin")) {
            lista = lista.stream()
                .filter(t -> t[3].equalsIgnoreCase(usuarioLogueado) && 
                             !t[6].equalsIgnoreCase("Cancelado") && 
                             !t[6].equalsIgnoreCase("Ausente"))
                .collect(Collectors.toList());
        }

        cargarTablaOrdenadaPorFecha(lista);
    }

    private void cargarTodosLosTurnosAdmin() {
        List<String[]> lista = turnoDAO.obtenerTodosLosTurnos();
        
        if (!usuarioLogueado.equalsIgnoreCase("admin")) {
            lista = lista.stream()
                .filter(t -> t[3].equalsIgnoreCase(usuarioLogueado) && 
                             !t[6].equalsIgnoreCase("Cancelado") && 
                             !t[6].equalsIgnoreCase("Ausente"))
                .collect(Collectors.toList());
        }

        cargarTablaOrdenadaPorFecha(lista);
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
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos para guardar el turno en la base de datos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
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

    private void enviarWhatsAppSinGuardar() {
        String cliente = txtCliente.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String barbero = (String) comboBarbero.getSelectedItem();
        String fecha = txtFecha.getText().trim();

        if (cliente.isEmpty() || telefono.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor completa al menos el nombre del cliente, el teléfono y la fecha de interés.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        telefono = telefono.replaceAll("[^0-9]", "");

        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El número de teléfono ingresado no es válido.", 
                "Teléfono inválido", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> turnosDelDia = turnoDAO.obtenerTurnosPorFecha(fecha);
        java.util.List<String> horariosLibres = new java.util.ArrayList<>(java.util.Arrays.asList(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
        ));

        for (String[] t : turnosDelDia) {
            String barberoTurno = t[3];
            String horaTurno = t[5];
            String estTurno = t[6];

            if (barberoTurno.equalsIgnoreCase(barbero) && !estTurno.equalsIgnoreCase("Cancelado")) {
                horariosLibres.remove(horaTurno);
            }
        }

        String textoDisponibles = horariosLibres.isEmpty() 
            ? "No hay horarios libres disponibles para esta fecha." 
            : String.join(", ", horariosLibres);

        String mensaje = "Hola *_" + cliente + "_*, te escribimos de la barbería. Para el día *_" + fecha + "_* tenemos los siguientes horarios disponibles:\n\n"
                + "🕒 *" + textoDisponibles + "*\n\n"
                + "¿Cuál de estos horarios te queda cómodo para agendar?";

        try {
            String mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8");
            String url = "https://wa.me/" + telefono + "?text=" + mensajeCodificado;

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al intentar abrir WhatsApp: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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
                this, 
                "Seleccione el nuevo estado:", 
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

    private void menuOpcionesWhatsApp() {
        int filaSeleccionada = tablaTurnos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona un turno de la tabla para enviar el mensaje.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {
            "1. Recordatorio y Solicitud de Confirmación",
            "2. Aviso de Horario Ocupado + Horarios Disponibles",
            "3. Confirmación de Turno Registrado"
        };

        String eleccion = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona el tipo de mensaje a enviar por WhatsApp:",
            "Mensajería WhatsApp",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (eleccion != null) {
            if (eleccion.startsWith("1")) {
                enviarWhatsAppMensaje(1, filaSeleccionada);
            } else if (eleccion.startsWith("2")) {
                enviarWhatsAppMensaje(2, filaSeleccionada);
            } else if (eleccion.startsWith("3")) {
                enviarWhatsAppMensaje(3, filaSeleccionada);
            }
        }
    }

    private void enviarWhatsAppMensaje(int tipoMensaje, int filaSeleccionada) {
        String cliente = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
        String telefono = modeloTabla.getValueAt(filaSeleccionada, 2).toString();
        String barbero = modeloTabla.getValueAt(filaSeleccionada, 3).toString();
        String fecha = modeloTabla.getValueAt(filaSeleccionada, 4).toString();
        String horario = modeloTabla.getValueAt(filaSeleccionada, 5).toString();

        telefono = telefono.replaceAll("[^0-9]", "");

        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El cliente seleccionado no cuenta con un número de teléfono válido.", 
                "Teléfono inválido", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensaje = "";

        switch (tipoMensaje) {
            case 1:
                mensaje = "Hola *_" + cliente + "_*, te escribimos de la barbería para recordarte que tenés un turno agendado para el día *_" + fecha + "_* a las *_" + horario + "_* hs.\n\n"
                        + "¿Nos confirmás si vas a asistir o si necesitás cancelarlo?";
                break;

            case 2:
                List<String[]> turnosDelDia = turnoDAO.obtenerTurnosPorFecha(fecha);
                java.util.List<String> horariosLibres = new java.util.ArrayList<>(java.util.Arrays.asList(
                    "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
                ));

                for (String[] t : turnosDelDia) {
                    String barberoTurno = t[3];
                    String horaTurno = t[5];
                    String estTurno = t[6];

                    if (barberoTurno.equalsIgnoreCase(barbero) && !estTurno.equalsIgnoreCase("Cancelado")) {
                        horariosLibres.remove(horaTurno);
                    }
                }

                String textoDisponibles = horariosLibres.isEmpty() 
                    ? "No hay horarios libres disponibles para esta fecha." 
                    : String.join(", ", horariosLibres);

                mensaje = "Hola *_" + cliente + "_*, lamentablemente ese horario no se encuentra disponible.\n\n"
                        + "Disponemos de los siguientes horarios para el *_" + fecha + "_*:\n"
                        + "🕒 *" + textoDisponibles + "*\n\n"
                        + "¿Te sirve alguno de estos turnos?";
                break;

            case 3:
                mensaje = "¡Hola *_" + cliente + "_*! Tu turno ha sido registrado con éxito para el día *_" + fecha + "_* a las *_" + horario + "_* hs.\n\n"
                        + "¡Te esperamos en la barbería!";
                break;
        }

        try {
            String mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8");
            String url = "https://wa.me/" + telefono + "?text=" + mensajeCodificado;

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al intentar abrir WhatsApp: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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