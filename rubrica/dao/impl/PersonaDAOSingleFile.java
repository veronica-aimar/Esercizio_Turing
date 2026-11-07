package rubrica.dao.impl;

import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import rubrica.model.Sessione;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;

// Implementazione che salva tutti i contatti di tutti gli utenti in un unico file "informazioni.txt"
public class PersonaDAOSingleFile implements PersonaDAO {
    private static final String NOME_FILE = "informazioni.txt";

    // Carica tutte le persone dal file, ma restituisce solo quelle dell'utente attualmente loggato
    @Override
    public ArrayList<Persona> caricaPersone() {
        ArrayList<Persona> lista = new ArrayList<>();
        File file = new File(NOME_FILE);
        if (!file.exists()) return lista;

        // Legge tutto il file e filtra solo le righe dell'utente attualmente loggato
        int utenteCorrente = Sessione.getIdUtenteCorrente();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] dati = linea.split(";");
                
                // 7 campi: utente_id;id_persona;nome;cognome;indirizzo;telefono;eta
                if (dati.length == 7) {
                    try {
                        int rowUtenteId = Integer.parseInt(dati[0].trim());
                        
                        // Caricamneto SOLO dei contatti dell'utente attualmente loggato
                        if (rowUtenteId == utenteCorrente) {
                            int idPersona = Integer.parseInt(dati[1].trim());
                            int eta = Integer.parseInt(dati[6].trim());
                            lista.add(new Persona(idPersona, dati[2].trim(), dati[3].trim(), dati[4].trim(), dati[5].trim(), eta));
                        }
                    } catch (NumberFormatException ignored) { }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Calcola il prossimo ID disponibile analizzando solo i contatti dell'utente corrente
    private int calcolaProssimoId() {
        int maxId = 0;
        for (Persona p : caricaPersone()) {
            if (p.getId() > maxId) {
                maxId = p.getId();
            }
        }
        return maxId + 1;
    }

    // Implementazione dei metodi CRUD per PersonaDAO
    @Override
    public boolean aggiungiPersona(Persona p) {
        p.setId(calcolaProssimoId());
        int utenteCorrente = Sessione.getIdUtenteCorrente();
        
        // Aggiunge la riga in fondo al file, anteponendo l'id dell'utente
        try (PrintStream ps = new PrintStream(new FileOutputStream(NOME_FILE, true))) {
            ps.println(utenteCorrente + ";" + p.formattaPerFile());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean aggiornaPersona(Persona p) {
        return modificaOEliminaSuFile(p, -1);
    }

    @Override
    public boolean eliminaPersona(int idPersona) {
        return modificaOEliminaSuFile(null, idPersona);
    }

    /* Metodo per riscrivere il file:
    Legge tutto il file (compresi i contatti degli altri utenti),
    sostituisce o salta (elimina) solo la riga dell'utente corrente,
    e riscrive tutto in modo sicuro. */
    private boolean modificaOEliminaSuFile(Persona pDaAggiornare, int idDaEliminare) {
        File file = new File(NOME_FILE);
        if (!file.exists()) return false;

        int utenteCorrente = Sessione.getIdUtenteCorrente();
        ArrayList<String> righeDaSalvare = new ArrayList<>();
        boolean modificato = false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] dati = linea.split(";");
                boolean rigaGestita = false;

                if (dati.length == 7) {
                    try {
                        int rowUtenteId = Integer.parseInt(dati[0].trim());
                        int rowPersonaId = Integer.parseInt(dati[1].trim());

                        // Se la riga appartiene all'utente loggato
                        if (rowUtenteId == utenteCorrente) {
                            
                            // AGGIORNAMENTO
                            if (pDaAggiornare != null && rowPersonaId == pDaAggiornare.getId()) {
                                righeDaSalvare.add(utenteCorrente + ";" + pDaAggiornare.formattaPerFile());
                                modificato = true;
                                rigaGestita = true;
                            } 
                            // ELIMINAZIONE
                            else if (idDaEliminare != -1 && rowPersonaId == idDaEliminare) {
                                // Salto la riga (non la aggiungo alla lista righeDaSalvare)
                                modificato = true;
                                rigaGestita = true;
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                }

                // Se la riga non appartiene all'utente loggato o non è quella da modificare/eliminare viene conservata esattamente com'era
                if (!rigaGestita) {
                    righeDaSalvare.add(linea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Se la riga è stata trovata e gestita sovrascrittura del file
        if (modificato) {
            try (PrintStream ps = new PrintStream(new File(NOME_FILE))) {
                for (String riga : righeDaSalvare) {
                    ps.println(riga);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        
        return false; // Il contatto non è stato trovato
    }
}