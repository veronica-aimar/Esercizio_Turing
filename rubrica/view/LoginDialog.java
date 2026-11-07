package rubrica.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// Classe per creare una finestra di dialogo di login con campi per username e password
public class LoginDialog extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private BottoneModerno btnLogin;

    public LoginDialog() {
        super("Autenticazione");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitolo = new JLabel("Accesso Rubrica");
        lblTitolo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitolo.setForeground(new Color(33, 37, 41));
        lblTitolo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(25, 0, 15, 0));
        add(lblTitolo, BorderLayout.NORTH);

        // Pannello centrale con campi di input per username e password
        JPanel panelCampi = new JPanel(new GridBagLayout());
        panelCampi.setBackground(Color.WHITE);
        panelCampi.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // CAMPI DI TESTO PER USERNAME E PASSWORD
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelCampi.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 32));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panelCampi.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelCampi.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 32));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        panelCampi.add(txtPassword, gbc);

        add(panelCampi, BorderLayout.CENTER);

        // Pannello inferiore con pulsante di login
        JPanel panelBottoni = new JPanel();
        panelBottoni.setBackground(Color.WHITE);
        panelBottoni.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        btnLogin = new BottoneModerno("ACCEDI", new Color(13, 110, 253), new Color(11, 94, 215));
        btnLogin.setPreferredSize(new Dimension(180, 42));

        panelBottoni.add(btnLogin);
        add(panelBottoni, BorderLayout.SOUTH);

        // SCORCIATOIE DA TASTIERA
        // INVIO corrisponde al pulsante Accedi
        getRootPane().setDefaultButton(btnLogin);

        // CANC svuota i campi di testo
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "svuotaCampi"
        );
        getRootPane().getActionMap().put("svuotaCampi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsername.setText("");
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public JButton getBtnLogin() { return btnLogin; }
}