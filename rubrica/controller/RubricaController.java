package rubrica.controller;

import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import rubrica.view.EditorPersonaDialog;
import rubrica.view.FinestraPrincipale;
import javax.swing.*;
import java.util.ArrayList;

// Controller per la gestione della logica dell'applicazione Rubrica, interagendo tra la vista (FinestraPrincipale) e il modello (PersonaDAO)
public class RubricaController {
    private FinestraPrincipale view;
    private PersonaDAO dao;
    private ArrayList<Persona> contatti;

    // Costruttore del controller della rubrica
    public RubricaController(FinestraPrincipale view, PersonaDAO dao) {
        this.view = view;
        this.dao = dao;
        this.contatti = dao.caricaPersone();
        
        inizializzaEventi();
        view.aggiornaTabella(contatti);
    }

    // Metodo privato per inizializzare gli eventi dei bottoni nella vista
    private void inizializzaEventi() {
        view.getBtnNuovo().addActionListener(e -> apriEditor(null, -1));
        
        // Gestione del bottone "Modifica Contatto"
        view.getBtnModifica().addActionListener(e -> {
            int riga = view.getRigaSelezionata();
            // Controllo se una riga è stata selezionata prima di procedere con la modifica
            if (riga == -1) {
                JOptionPane.showMessageDialog(view, "Per modificare è necessario prima selezionare una persona.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            apriEditor(contatti.get(riga), riga);
        });

        // Gestione del bottone "Elimina Contatto"
        view.getBtnElimina().addActionListener(e -> {
            int riga = view.getRigaSelezionata();
            // Controllo se una riga è stata selezionata prima di procedere con l'eliminazione
            if (riga == -1) {
                JOptionPane.showMessageDialog(view, "Per eliminare è necessario prima selezionare una persona.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Conferma dell'eliminazione della persona selezionata
            Persona p = contatti.get(riga);
            int conferma = JOptionPane.showConfirmDialog(view, "Eliminare la persona " + p.getNome() + " " + p.getCognome() + "?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                contatti.remove(riga);
                dao.salvaPersone(contatti);
                view.aggiornaTabella(contatti);
            }
        });
    }

    // Metodo privato per aprire l'editor di persona, sia per la creazione di una nuova persona che per la modifica di una esistente
    private void apriEditor(Persona p, int indice) {
        EditorPersonaDialog dialog = new EditorPersonaDialog(view, p == null ? "Nuova Persona" : "Modifica Persona");
        if (p != null) dialog.popolaCampi(p);

        // Gestione dei bottoni del dialogo dell'editor
        dialog.getBtnAnnulla().addActionListener(e -> dialog.dispose());
        dialog.getBtnSalva().addActionListener(e -> {
            // Estrazione dei dati grezzi
            String nome = dialog.getNomeRaw();
            String cognome = dialog.getCognomeRaw();
            String indirizzo = dialog.getIndirizzoRaw();
            String telefono = dialog.getTelefonoRaw();
            String eta = dialog.getEtaRaw();

            // Esecuzione della validazione
            String errore = ValidatorePersona.valida(nome, cognome, indirizzo, telefono, eta);
            if (errore != null) {
                JOptionPane.showMessageDialog(dialog, errore, "Dati non validi", JOptionPane.WARNING_MESSAGE);
                return; // Blocco del salvataggio finché l'utente non corregge
            }

            // Controllo che il numero non sia già presente in rubrica
            Persona personaConStessoNumero = null;
            for (int i = 0; i < contatti.size(); i++) {
                if (i != indice && contatti.get(i).getTelefono().equals(telefono)) {
                    personaConStessoNumero = contatti.get(i);
                    break;
                }
            }

            if (personaConStessoNumero != null) {
                // In caso positivo avviso all'utente e chiedo conferma prima di procedere con il salvataggio
                int risposta = JOptionPane.showConfirmDialog(dialog,
                    "Il numero " + telefono + " è già associato a " 
                    + personaConStessoNumero.getNome() + " " + personaConStessoNumero.getCognome() 
                    + ".\nVuoi procedere comunque con il salvataggio?",
                    "Possibile Duplicato",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (risposta != JOptionPane.YES_OPTION) {
                    return; // L'utente decide di non salvare
                }
            }

            // Salvataggio se i dati sono corretti
            Persona personaValidata = dialog.getDatiInseriti();
            dialog.dispose();

            if (indice == -1) {
                contatti.add(personaValidata);
            } else {
                contatti.set(indice, personaValidata);
            }
            
            view.aggiornaTabella(contatti);
            dao.salvaPersone(contatti);
        });
        dialog.setVisible(true);
    }
}