import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public VentanaLogin() {
        setTitle("Login - Barbería");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel(" Usuario:"));
        txtUsuario = new JTextField();
        add(txtUsuario);

        add(new JLabel(" Contraseña:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        btnIngresar = new JButton("Ingresar");
        add(new JLabel(""));
        add(btnIngresar);

        txtUsuario.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> btnIngresar.doClick());

        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if ((usuario.equalsIgnoreCase("admin") || usuario.equalsIgnoreCase("Carlos") || usuario.equalsIgnoreCase("Lucas")) 
                    && password.equals("1234")) {
                    
                    JOptionPane.showMessageDialog(null, "¡Bienvenido, " + usuario + "!");
                    new VentanaPrincipal(usuario).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}