package rubrica.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// Classe per gestire sia il login che la registrazione dinamica in un'unica finestra
public class LoginDialog extends JFrame {
    private JLabel lblTitolo;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblConfermaPassword;
    private JPasswordField txtConfermaPassword;
    private BottoneModerno btnAzione;
    private JButton btnSwitchModalita;

    private boolean modalitaRegistrazione = false;

    // Costruttore della finestra di login/registrazione
    public LoginDialog() {
        super("Autenticazione");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Intestazione
        lblTitolo = new JLabel("Accesso Rubrica");
        lblTitolo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitolo.setForeground(new Color(33, 37, 41));
        lblTitolo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(25, 0, 15, 0));
        add(lblTitolo, BorderLayout.NORTH);

        // Pannello centrale con i campi
        JPanel panelCampi = new JPanel(new GridBagLayout());
        panelCampi.setBackground(Color.WHITE);
        panelCampi.setBorder(BorderFactory.createEmptyBorder(10, 30, 15, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Campo Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        panelCampi.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 32));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        panelCampi.add(txtUsername, gbc);

        // Campo Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        panelCampi.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 32));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.65;
        panelCampi.add(txtPassword, gbc);

        // Campo Ripeti Password (nascosto di default, visibile solo in registrazione)
        lblConfermaPassword = new JLabel("Ripeti Password:");
        lblConfermaPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblConfermaPassword.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        panelCampi.add(lblConfermaPassword, gbc);

        txtConfermaPassword = new JPasswordField(15);
        txtConfermaPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfermaPassword.setPreferredSize(new Dimension(200, 32));
        txtConfermaPassword.setVisible(false);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.65;
        panelCampi.add(txtConfermaPassword, gbc);

        add(panelCampi, BorderLayout.CENTER);

        // Pannello inferiore: bottone principale + link per cambio modalità
        JPanel panelInferiore = new JPanel();
        panelInferiore.setLayout(new BoxLayout(panelInferiore, BoxLayout.Y_AXIS));
        panelInferiore.setBackground(Color.WHITE);
        panelInferiore.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Bottone principale (ACCEDI / REGISTRATI)
        btnAzione = new BottoneModerno("ACCEDI", new Color(13, 110, 253), new Color(11, 94, 215));
        btnAzione.setPreferredSize(new Dimension(220, 42));
        btnAzione.setMaximumSize(new Dimension(220, 42));
        btnAzione.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Link cliccabile per alternare Login / Registrazione
        btnSwitchModalita = new JButton("Non hai un account? Registrati");
        btnSwitchModalita.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSwitchModalita.setForeground(new Color(13, 110, 253));
        btnSwitchModalita.setContentAreaFilled(false);
        btnSwitchModalita.setBorderPainted(false);
        btnSwitchModalita.setFocusPainted(false);
        btnSwitchModalita.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSwitchModalita.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSwitchModalita.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        btnSwitchModalita.addActionListener(e -> alternaModalita());

        panelInferiore.add(btnAzione);
        panelInferiore.add(btnSwitchModalita);
        add(panelInferiore, BorderLayout.SOUTH);

        // Scorciatoie da tastiera
        getRootPane().setDefaultButton(btnAzione);

        // CANC svuota i campi di testo
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "svuotaCampi"
        );
        getRootPane().getActionMap().put("svuotaCampi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                svuotaCampi();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    // Alterna dinamicamente l'interfaccia tra Login e Registrazione
    public void alternaModalita() {
        modalitaRegistrazione = !modalitaRegistrazione;

        lblConfermaPassword.setVisible(modalitaRegistrazione);
        txtConfermaPassword.setVisible(modalitaRegistrazione);
        txtConfermaPassword.setText("");

        if (modalitaRegistrazione) {
            setTitle("Registrazione");
            lblTitolo.setText("Nuovo Account");
            btnAzione = new BottoneModerno("REGISTRATI", new Color(25, 135, 84), new Color(21, 115, 71)); // Verde
            btnSwitchModalita.setText("Hai già un account? Accedi");
        } else {
            setTitle("Autenticazione");
            lblTitolo.setText("Accesso Rubrica");
            btnAzione = new BottoneModerno("ACCEDI", new Color(13, 110, 253), new Color(11, 94, 215)); // Blu
            btnSwitchModalita.setText("Non hai un account? Registrati");
        }

        pack();
        setLocationRelativeTo(null);
    }

    // Svuota tutti i campi di testo e riporta il focus sul campo Username
    public void svuotaCampi() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfermaPassword.setText("");
        txtUsername.requestFocus();
    }

    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getConfermaPassword() { return new String(txtConfermaPassword.getPassword()); }
    public boolean isModalitaRegistrazione() { return modalitaRegistrazione; }
    public JButton getBtnAzione() { return btnAzione; }
}