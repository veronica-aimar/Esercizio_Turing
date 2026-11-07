package rubrica.dao.impl;

import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import rubrica.model.Sessione;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;

// Implementazione MultiFile multi-utente (salva in sottocartelle basate sull'ID utente)
public class PersonaDAOMultiFile implements PersonaDAO {
    private static final String CARTELLA_BASE = "informazioni";

    // Restituisce la cartella specifica dell'utente corrente
    // Creandola se non esiste (es: "informazioni/utente_1")
    private File getCartellaUtente() {
        int utenteId = Sessione.getIdUtenteCorrente();
        
        File dirBase = new File(CARTELLA_BASE);
        if (!dirBase.exists()) dirBase.mkdir();
        
        File dirUtente = new File(dirBase, "utente_" + utenteId);
        if (!dirUtente.exists()) dirUtente.mkdir();
        
        return dirUtente;
    }

    // Carica tutte le persone dell'utente corrente dalla sua cartella
    @Override
    public ArrayList<Persona> caricaPersone() {
        ArrayList<Persona> lista = new ArrayList<>();
        File dirUtente = getCartellaUtente(); // Legge SOLO dalla cartella dell'utente

        // Legge tutti i file .txt nella cartella dell'utente e li converte in oggetti Persona
        File[] files = dirUtente.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try (Scanner scanner = new Scanner(file)) {
                    if (scanner.hasNextLine()) {
                        String[] dati = scanner.nextLine().split(";");
                        if (dati.length == 6) {
                            try {
                                int id = Integer.parseInt(dati[0].trim());
                                int eta = Integer.parseInt(dati[5].trim());
                                lista.add(new Persona(id, dati[1].trim(), dati[2].trim(), dati[3].trim(), dati[4].trim(), eta));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        return lista;
    }

    // Calcola il prossimo ID leggendo solo i file dell'utente corrente
    private int calcolaProssimoId() {
        int maxId = 0;
        File dirUtente = getCartellaUtente();
        
        File[] files = dirUtente.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try {
                    String name = file.getName();
                    int indiceTrattino = name.indexOf('-');
                    if (indiceTrattino != -1) {
                        int idCorrente = Integer.parseInt(name.substring(0, indiceTrattino));
                        if (idCorrente > maxId) maxId = idCorrente;
                    }
                } catch (Exception ignored) {}
            }
        }
        return maxId + 1;
    }

    // Genera un nome file sicuro per la persona, basato su ID, nome e cognome
    private String generaNomeFile(Persona p) {
        return p.getId() + "-" + sanificaNomeFile(p.getNome()) + "-" + sanificaNomeFile(p.getCognome()) + ".txt";
    }

    // Implementazione dei metodi CRUD per PersonaDAO
    @Override
    public boolean aggiungiPersona(Persona p) {
        File dirUtente = getCartellaUtente();
        p.setId(calcolaProssimoId());
        
        File file = new File(dirUtente, generaNomeFile(p));
        return scriviSuFile(file, p);
    }

    @Override
    public boolean aggiornaPersona(Persona p) {
        File dirUtente = getCartellaUtente();
        
        // Elimina il vecchio file
        eliminaPersona(p.getId());
        
        // Crea il nuovo file
        File nuovoFile = new File(dirUtente, generaNomeFile(p));
        return scriviSuFile(nuovoFile, p);
    }

    @Override
    public boolean eliminaPersona(int idPersona) {
        File dirUtente = getCartellaUtente();

        File[] fileTrovati = dirUtente.listFiles((d, name) -> name.startsWith(idPersona + "-"));
        if (fileTrovati != null && fileTrovati.length > 0) {
            return fileTrovati[0].delete();
        }
        return false;
    }

    private boolean scriviSuFile(File file, Persona p) {
        try (PrintStream ps = new PrintStream(file)) {
            ps.println(p.formattaPerFile());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String sanificaNomeFile(String testo) {
        if (testo == null || testo.trim().isEmpty()) {
            return "senzanome";
        }
        String sanificato = testo.replaceAll("[\\\\/:*?\"<>|\\x00-\\x1F]", "_");
        sanificato = sanificato.replaceAll("[ .]+$", "");
        if (sanificato.matches("(?i)^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])$")) {
            sanificato = "_" + sanificato;
        }
        return sanificato.isEmpty() ? "contatto" : sanificato;
    }
}