package rubrica.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

// Bottone moderno con effetti visivi personalizzati per hover e click, con bordo arrotondato
public class BottoneModerno extends JButton {
    private Color coloreBase;
    private Color coloreHover;
    private Color colorePremuto;

    /* Costruttore per il bottone moderno
        @param testo il testo del bottone
        @param coloreBase il colore del bottone in stato normale
        @param coloreHover il colore del bottone in stato di hover
        @param coloreTesto il colore del testo del bottone */
    public BottoneModerno(String testo, Color coloreBase, Color coloreHover, Color coloreTesto) {
        super(testo);
        this.coloreBase = coloreBase;
        this.coloreHover = coloreHover;
        this.colorePremuto = coloreHover.darker();

        // Staccamento del bottone dal Look and Feel di Windows per evitare che si sbianchi
        setUI(new BasicButtonUI());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        setForeground(coloreTesto);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
    }

    /* Costruttore con testo bianco di default
        @param testo il testo del bottone
        @param coloreBase il colore del bottone in stato normale
        @param coloreHover il colore del bottone in stato di hover */
    public BottoneModerno(String testo, Color coloreBase, Color coloreHover) {
        this(testo, coloreBase, coloreHover, Color.WHITE);
    }

    // Override del metodo paintComponent per disegnare il bottone con effetti visivi personalizzati
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Abilitazione dell'anti-aliasing per bordi più morbidi
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gestione degli stati: cliccato, hover, normale
        if (getModel().isPressed()) {
            g2.setColor(colorePremuto);
        } else if (getModel().isRollover()) {
            g2.setColor(coloreHover);
        } else {
            g2.setColor(coloreBase);
        }

        // Bordo arrotondato a 8px
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();

        super.paintComponent(g);
    }
}