package rubrica.view;

import rubrica.model.Persona;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// Dialog per l'editing dei dati di una persona
public class EditorPersonaDialog extends JDialog {
    private JTextField txtNome, txtCognome, txtIndirizzo, txtTelefono, txtEta;
    private BottoneModerno btnSalva, btnAnnulla;

    /* Costruttore della finestra di editing
        @param parent la finestra principale che apre questa finestra
        @param titolo il titolo della finestra */
    public EditorPersonaDialog(Frame parent, String titolo) {
        super(parent, titolo, true);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Impostazioni della finestra
        JLabel lblTitolo = new JLabel(titolo.toUpperCase());
        lblTitolo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitolo.setForeground(new Color(50, 50, 50));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        add(lblTitolo, BorderLayout.NORTH);

        // Inizializzazione dei campi di testo e dei bottoni
        JPanel panelCampi = new JPanel(new GridBagLayout());
        panelCampi.setBackground(Color.WHITE);
        panelCampi.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Aggiunta dei campi di testo con etichette
        txtNome = aggiungiCampo("Nome:", panelCampi, gbc, 0);
        txtCognome = aggiungiCampo("Cognome:", panelCampi, gbc, 1);
        txtIndirizzo = aggiungiCampo("Indirizzo:", panelCampi, gbc, 2);
        txtTelefono = aggiungiCampo("Telefono:", panelCampi, gbc, 3);
        txtEta = aggiungiCampo("Età:", panelCampi, gbc, 4);

        add(panelCampi, BorderLayout.CENTER);

        // Pannello dei bottoni con layout a destra e bordo superiore
        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        panelBottoni.setBackground(new Color(248, 249, 250));
        panelBottoni.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 230, 235)));

        // Creazione dei bottoni Salva e Annulla con stili personalizzati
        // Bottone Annulla con icona e dimensioni personalizzate
        btnAnnulla = new BottoneModerno("Annulla", new Color(222, 226, 230), new Color(206, 212, 218), new Color(33, 37, 41));
        btnAnnulla.setIcon(Icone.creaIconaAnnulla(14, new Color(33, 37, 41)));
        btnAnnulla.setIconTextGap(8);
        btnAnnulla.setPreferredSize(new Dimension(115, 38));

        // Bottone Salva con icona e dimensioni personalizzate
        btnSalva = new BottoneModerno("Salva Contatto", new Color(13, 110, 253), new Color(11, 94, 215));
        btnSalva.setIcon(Icone.creaIconaSalva(16, Color.WHITE));
        btnSalva.setIconTextGap(8);
        btnSalva.setPreferredSize(new Dimension(160, 38));

        panelBottoni.add(btnAnnulla);
        panelBottoni.add(btnSalva);
        add(panelBottoni, BorderLayout.SOUTH);

        // SCORCIATOIE DA TASTIERA
        // INVIO preme aziona il bottone Salva
        getRootPane().setDefaultButton(btnSalva);

        // ESC o CANC (se la finestra ha il focus) azionano Annulla
        Action annullaAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAnnulla.doClick();
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "annulla"
        );
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "annulla"
        );
        getRootPane().getActionMap().put("annulla", annullaAction);

        pack();
        setLocationRelativeTo(parent);
    }

    /* Aggiunge un campo di testo con un'etichetta
        @param labelTesto il testo dell'etichetta
        @param panel il pannello in cui aggiungere il campo
        @param gbc le impostazioni del layout
        @param riga la riga in cui posizionare il campo
        @return il campo di testo creato */
    private JTextField aggiungiCampo(String labelTesto, JPanel panel, GridBagConstraints gbc, int riga) {
        JLabel label = new JLabel(labelTesto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = riga; gbc.weightx = 0.3;
        panel.add(label, gbc);

        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(220, 32));
        gbc.gridx = 1; gbc.gridy = riga; gbc.weightx = 0.7;
        panel.add(textField, gbc);

        return textField;
    }

    /* Popola i campi con i dati della persona.
        @param p la persona di cui popolare i campi */
    public void popolaCampi(Persona p) {
        // Popola i campi di testo con i dati della persona
        txtNome.setText(p.getNome());
        txtCognome.setText(p.getCognome());
        txtIndirizzo.setText(p.getIndirizzo());
        txtTelefono.setText(p.getTelefono());
        
        // Se l'età non è impostata (<= 0) si lascia la casella di testo vuota
        if (p.getEta() > 0) {
            txtEta.setText(String.valueOf(p.getEta()));
        } else {
            txtEta.setText("");
        }
    }

    // Getters per ottenere i dati inseriti nei campi di testo
    public String getNomeRaw() { return txtNome.getText().trim(); }
    public String getCognomeRaw() { return txtCognome.getText().trim(); }
    public String getIndirizzoRaw() { return txtIndirizzo.getText().trim(); }
    public String getTelefonoRaw() { return txtTelefono.getText().trim(); }
    public String getEtaRaw() { return txtEta.getText().trim(); }

    // Restituisce un oggetto Persona con i dati inseriti nei campi di testo
    public Persona getDatiInseriti() {
        int etaVal = 0;
        // Se il campo età non è vuoto prova a convertire il testo in un intero
        String etaTesto = txtEta.getText().trim();
        if (!etaTesto.isEmpty()) {
            etaVal = Integer.parseInt(etaTesto);
        }

        return new Persona(
            txtNome.getText().trim(),
            txtCognome.getText().trim(),
            txtIndirizzo.getText().trim(),
            txtTelefono.getText().trim(),
            etaVal
        );
    }

    public JButton getBtnSalva() { return btnSalva; }
    public JButton getBtnAnnulla() { return btnAnnulla; }
}