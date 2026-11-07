package rubrica.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

// Classe per creare icone vettoriali personalizzate per l'interfaccia utente
public class Icone {

    // Crea un'icona a forma di + con dimensione e colore specificati
    public static Icon creaIconaPiu(int dim, Color colore) {
        return new IconaVettoriale(dim, (g2, w, h) -> {
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int midX = w / 2;
            int midY = h / 2;
            int span = w / 3;
            g2.drawLine(midX - span, midY, midX + span, midY);
            g2.drawLine(midX, midY - span, midX, midY + span);
        });
    }

    // Crea un'icona di modifica con dimensione e colore specificati
    public static Icon creaIconaModifica(int dim, Color colore) {
        return new IconaVettoriale(dim, (g2, w, h) -> {
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Corpo e punta matita
            g2.drawLine(3, h - 4, 6, h - 4);
            g2.drawLine(3, h - 4, 3, h - 7);
            g2.drawLine(4, h - 8, w - 6, 4);
            g2.drawLine(7, h - 5, w - 3, 7);
            g2.drawLine(w - 6, 4, w - 3, 7);
        });
    }

    // Crea un'icona di cestino con dimensione e colore specificati
    public static Icon creaIconaCestino(int dim, Color colore) {
        return new IconaVettoriale(dim, (g2, w, h) -> {
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Coperchio cestino
            g2.drawLine(3, 4, w - 4, 4);
            g2.drawLine(w / 2 - 2, 2, w / 2 + 2, 2);
            // Corpo cestino
            g2.drawRoundRect(4, 5, w - 9, h - 7, 2, 2);
            g2.drawLine(7, 8, 7, h - 5);
            g2.drawLine(w - 8, 8, w - 8, h - 5);
        });
    }

    // Crea un'icona di salvataggio con dimensione e colore specificati
    public static Icon creaIconaSalva(int dim, Color colore) {
        return new IconaVettoriale(dim, (g2, w, h) -> {
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Spunta di conferma
            Path2D.Float check = new Path2D.Float();
            check.moveTo(3, h * 0.52f);
            check.lineTo(w * 0.42f, h * 0.82f);
            check.lineTo(w - 3, h * 0.25f);
            g2.draw(check);
        });
    }

    // Crea un'icona di annullamento con dimensione e colore specificati
    public static Icon creaIconaAnnulla(int dim, Color colore) {
        return new IconaVettoriale(dim, (g2, w, h) -> {
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int pad = 4;
            g2.drawLine(pad, pad, w - pad, h - pad);
            g2.drawLine(w - pad, pad, pad, h - pad);
        });
    }

    // Interfaccia e classe interna per il disegno dinamico
    private interface DisegnatoreIcona {
        void disegna(Graphics2D g2, int width, int height);
    }

    private static class IconaVettoriale implements Icon {
        private final int dimensione;
        private final DisegnatoreIcona disegnatore;

        public IconaVettoriale(int dimensione, DisegnatoreIcona disegnatore) {
            this.dimensione = dimensione;
            this.disegnatore = disegnatore;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            disegnatore.disegna(g2, dimensione, dimensione);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return dimensione; }

        @Override
        public int getIconHeight() { return dimensione; }
    }
}