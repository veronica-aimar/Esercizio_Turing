package rubrica.view;

import rubrica.model.Persona;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

// Classe principale della GUI dell'applicazione Rubrica, estende JFrame e gestisce l'interfaccia utente principale
public class FinestraPrincipale extends JFrame {
    private JTable tabella;
    private DefaultTableModel modelloTabella;
    private BottoneModerno btnNuovo, btnModifica, btnElimina;

    public FinestraPrincipale() {
        super("Rubrica Telefonica - Accesso Effettuato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        toolBar.setBackground(new Color(248, 249, 250));

        // Creazione dei bottoni con icone personalizzate e colori specificati
        // Bottone "Nuovo Contatto"
        btnNuovo = new BottoneModerno("Nuovo Contatto", new Color(25, 135, 84), new Color(21, 115, 71));
        btnNuovo.setIcon(Icone.creaIconaPiu(16, Color.WHITE));
        btnNuovo.setIconTextGap(8);

        // Bottone "Modifica Contatto"
        btnModifica = new BottoneModerno("Modifica Contatto", new Color(13, 110, 253), new Color(11, 94, 215));
        btnModifica.setIcon(Icone.creaIconaModifica(16, Color.WHITE));
        btnModifica.setIconTextGap(8);

        // Bottone "Elimina Contatto"
        btnElimina = new BottoneModerno("Elimina Contatto", new Color(220, 53, 69), new Color(187, 45, 59));
        btnElimina.setIcon(Icone.creaIconaCestino(16, Color.WHITE));
        btnElimina.setIconTextGap(8);

        toolBar.add(btnNuovo);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnModifica);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnElimina);

        add(toolBar, BorderLayout.NORTH);

        // Configurazione della tabella per visualizzare i contatti
        modelloTabella = new DefaultTableModel(new String[]{"Nome", "Cognome", "Telefono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Creazione della tabella con il modello e personalizzazione dell'aspetto
        tabella = new JTable(modelloTabella);
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.setRowHeight(36);
        tabella.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabella.setShowVerticalLines(false);
        tabella.setIntercellSpacing(new Dimension(0, 0));

        // Personalizzazione dell'intestazione della tabella
        JTableHeader header = tabella.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(233, 236, 239));
        header.setForeground(new Color(33, 37, 41));
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        // Aggiunta della tabella all'interno di uno JScrollPane con bordi e sfondo personalizzati
        JScrollPane scrollPane = new JScrollPane(tabella);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // SCORCIATOIE DA TASTIERA NELLA SCHERMATA PRINCIPALE
        // Tasto CANC: attiva il bottone Elimina Contatto
        tabella.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminaContatto"
        );
        tabella.getActionMap().put("eliminaContatto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnElimina.doClick();
            }
        });

        // Tasto INVIO: attiva il bottone Modifica Contatto
        tabella.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "modificaContatto"
        );
        tabella.getActionMap().put("modificaContatto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnModifica.doClick();
            }
        });
    }

    // Metodo per aggiornare la tabella con i dati dei contatti forniti
    public void aggiornaTabella(ArrayList<Persona> contatti) {
        modelloTabella.setRowCount(0);
        for (Persona p : contatti) {
            modelloTabella.addRow(new Object[]{p.getNome(), p.getCognome(), p.getTelefono()});
        }
    }

    // Metodo per ottenere l'indice della riga selezionata nella tabella
    public int getRigaSelezionata() { return tabella.getSelectedRow(); }
    public JButton getBtnNuovo() { return btnNuovo; }
    public JButton getBtnModifica() { return btnModifica; }
    public JButton getBtnElimina() { return btnElimina; }
}