import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    private JTable tablaTurnos;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoTurno, btnCambiarEstado, btnCerrarSesion;
    private String usuarioLogueado;

    public VentanaPrincipal(String usuario) {
        this.usuarioLogueado = usuario;

        setTitle("Barbería - Panel de Gestión (" + usuarioLogueado + ")");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Agenda de Turnos - Sesión: " + usuarioLogueado);
        lblTitulo.setBounds(20, 20, 400, 25);
        add(lblTitulo);

        String[] columnas = {"ID", "Cliente", "Teléfono", "Barbero", "Horario", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        
        // Datos de ejemplo iniciales
        modeloTabla.addRow(new Object[]{"1", "Carlos Pérez", "2634567890", "Barbero 1", "10:00", "PENDIENTE"});
        modeloTabla.addRow(new Object[]{"2", "Juan Gómez", "2631234567", "Barbero 2", "10:30", "PRESENTE"});
        modeloTabla.addRow(new Object[]{"3", "Pedro López", "2639876543", "Barbero 1", "11:00", "PENDIENTE"});

        // Filtrar según el rol (admin ve todo, barbero1 ve Barbero 1, barbero2 ve Barbero 2)
        DefaultTableModel modeloFiltrado = new DefaultTableModel(columnas, 0);
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object[] fila = {
                modeloTabla.getValueAt(i, 0),
                modeloTabla.getValueAt(i, 1),
                modeloTabla.getValueAt(i, 2),
                modeloTabla.getValueAt(i, 3),
                modeloTabla.getValueAt(i, 4),
                modeloTabla.getValueAt(i, 5)
            };
            
            if (usuarioLogueado.equals("admin")) {
                modeloFiltrado.addRow(fila);
            } else if (usuarioLogueado.equals("barbero1") && fila[3].toString().equalsIgnoreCase("Barbero 1")) {
                modeloFiltrado.addRow(fila);
            } else if (usuarioLogueado.equals("barbero2") && fila[3].toString().equalsIgnoreCase("Barbero 2")) {
                modeloFiltrado.addRow(fila);
            }
        }

        tablaTurnos = new JTable(modeloFiltrado);
        JScrollPane scrollPane = new JScrollPane(tablaTurnos);
        scrollPane.setBounds(20, 60, 690, 220);
        add(scrollPane);

        btnNuevoTurno = new JButton("Nuevo Turno");
        btnNuevoTurno.setBounds(20, 310, 150, 30);
        add(btnNuevoTurno);

        // Si no es admin, se deshabilita el botón de crear nuevos turnos
        if (!usuarioLogueado.equals("admin")) {
            btnNuevoTurno.setEnabled(false);
        }

        btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.setBounds(180, 310, 150, 30);
        add(btnCambiarEstado);

        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(560, 310, 150, 30);
        add(btnCerrarSesion);

        // Acción del botón Nuevo Turno (Solo para Admin)
        btnNuevoTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField txtCliente = new JTextField();
                JTextField txtTelefono = new JTextField();
                String[] barberosDisponibles = {"Barbero 1", "Barbero 2"};
                JComboBox<String> cmbBarbero = new JComboBox<>(barberosDisponibles);
                JTextField txtHorario = new JTextField();

                Object[] camposFormulario = {
                    "Nombre del Cliente:", txtCliente,
                    "Teléfono:", txtTelefono,
                    "Barbero Asignado:", cmbBarbero,
                    "Horario (ej. 11:00):", txtHorario
                };

                int opcion = JOptionPane.showConfirmDialog(
                    null, camposFormulario, "Registrar Nuevo Turno", JOptionPane.OK_CANCEL_OPTION
                );

                if (opcion == JOptionPane.OK_OPTION) {
                    String cliente = txtCliente.getText().trim();
                    String telefono = txtTelefono.getText().trim();
                    String barbero = (String) cmbBarbero.getSelectedItem();
                    String horario = txtHorario.getText().trim();

                    if (!cliente.isEmpty() && !telefono.isEmpty() && !horario.isEmpty()) {
                        boolean solapado = false;
                        for (int i = 0; i < modeloFiltrado.getRowCount(); i++) {
                            if (modeloFiltrado.getValueAt(i, 3).toString().equalsIgnoreCase(barbero) && 
                                modeloFiltrado.getValueAt(i, 4).toString().equalsIgnoreCase(horario) && 
                                !modeloFiltrado.getValueAt(i, 5).toString().equalsIgnoreCase("CANCELADO")) {
                                solapado = true;
                                break;
                            }
                        }

                        if (solapado) {
                            JOptionPane.showMessageDialog(null, "Conflicto: El barbero ya tiene un turno a esa hora.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            int id = modeloFiltrado.getRowCount() + 1;
                            modeloFiltrado.addRow(new Object[]{String.valueOf(id), cliente, telefono, barbero, horario, "PENDIENTE"});
                            JOptionPane.showMessageDialog(null, "¡Turno registrado con éxito!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Acción del botón Cambiar Estado (Incluye PENDIENTE, PRESENTE, ATENDIDO, AUSENTE, CANCELADO)
        btnCambiarEstado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaTurnos.getSelectedRow();
                
                if (filaSeleccionada != -1) {
                    String[] estados = {"PENDIENTE", "PRESENTE", "ATENDIDO", "AUSENTE", "CANCELADO"};
                    String nuevoEstado = (String) JOptionPane.showInputDialog(
                        null, "Seleccione el nuevo estado:", "Cambiar Estado",
                        JOptionPane.QUESTION_MESSAGE, null, estados, estados[0]
                    );

                    if (nuevoEstado != null) {
                        modeloFiltrado.setValueAt(nuevoEstado, filaSeleccionada, 5);
                        JOptionPane.showMessageDialog(null, "¡Estado actualizado con éxito!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, seleccione un turno de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Acción del botón Cerrar Sesión (Vuelve al Login)
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaLogin().setVisible(true);
                dispose();
            }
        });
    }
}