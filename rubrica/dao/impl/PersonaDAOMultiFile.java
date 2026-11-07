package rubrica.dao.impl;

import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Vector;

// Implementazione di PersonaDAO che salva ogni persona in un file separato nella cartella "informazioni"
public class PersonaDAOMultiFile implements PersonaDAO {
    private static final String CARTELLA = "informazioni";

    // Carica tutte le persone dai file nella cartella "informazioni"
    @Override
    public Vector<Persona> caricaPersone() {
        Vector<Persona> lista = new Vector<>();
        File dir = new File(CARTELLA);
        if (!dir.exists() || !dir.isDirectory()) return lista;

        // Filtra i file nella cartella per ottenere solo quelli con estensione .txt
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try (Scanner scanner = new Scanner(file)) {
                    if (scanner.hasNextLine()) {
                        String[] dati = scanner.nextLine().split(";");
                        if (dati.length == 5) {
                            int etaVal = 0;
                            String etaStr = dati[4].trim();
                            if (!etaStr.isEmpty()) {
                                try {
                                    etaVal = Integer.parseInt(etaStr);
                                } catch (NumberFormatException ignored) {}
                            }
                            lista.add(new Persona(dati[0].trim(), dati[1].trim(), dati[2].trim(), dati[3].trim(), etaVal));
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        return lista;
    }

    // Salva tutte le persone nei file nella cartella "informazioni"
    @Override
    public void salvaPersone(Vector<Persona> persone) {
        File dir = new File(CARTELLA);
        if (!dir.exists()) dir.mkdir();

        // Pulizia dei file precedenti
        File[] oldFiles = dir.listFiles();
        if (oldFiles != null) {
            for (File f : oldFiles) f.delete();
        }

        for (Persona p : persone) {
            // Sanificazione dei singoli campi
            String nomePulito = sanificaNomeFile(p.getNome());
            String cognomePulito = sanificaNomeFile(p.getCognome());

            String nomeFile = nomePulito + "-" + cognomePulito + ".txt";
            File file = new File(dir, nomeFile);

            try (PrintStream ps = new PrintStream(file)) {
                ps.println(p.formattaPerFile());
            } catch (Exception e) {
                System.err.println("Errore durante il salvataggio del file " + nomeFile + ": " + e.getMessage());
            }
        }
    }

    // Metodo per la sanificazione (rimozione dei caratteri non consentiti per un nome di file) dei nomi delle persone
    private String sanificaNomeFile(String testo) {
        if (testo == null || testo.trim().isEmpty()) {
            return "senza-nome";
        }

        // Rimozione dei caratteri vietati su Windows e Unix: \ / : * ? " < > | e caratteri di controllo
        String sanificato = testo.replaceAll("[\\\\/:*?\"<>|\\x00-\\x1F]", "_");

        // Rimozione di spazi e punti finali (non ammessi da Windows a fine nome file)
        sanificato = sanificato.replaceAll("[ .]+$", "");

        // Verifica dei nomi riservati di Windows (es. CON, PRN, AUX, NUL, COM1, LPT1...)
        if (sanificato.matches("(?i)^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])$")) {
            sanificato = "_" + sanificato;
        }

        // Se il nome risulta completamente vuoto dopo la pulizia, assegna un fallback
        return sanificato.isEmpty() ? "contatto" : sanificato;
    }
}