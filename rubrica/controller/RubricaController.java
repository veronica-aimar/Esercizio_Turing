package rubrica.controller;

import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import rubrica.view.EditorPersonaDialog;
import rubrica.view.FinestraPrincipale;
import rubrica.view.LoginDialog;

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

        view.getBtnLogout().addActionListener(e -> eseguiLogout());
        
        // Gestione del bottone "Modifica Contatto"
        view.getBtnModifica().addActionListener(e -> {
            int riga = view.getRigaSelezionata();
            if (riga == -1) {
                JOptionPane.showMessageDialog(view, "Per modificare è necessario prima selezionare una persona.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            apriEditor(contatti.get(riga), riga);
        });

        // Gestione del bottone "Elimina Contatto"
        view.getBtnElimina().addActionListener(e -> {
            int riga = view.getRigaSelezionata();
            if (riga == -1) {
                JOptionPane.showMessageDialog(view, "Per eliminare è necessario prima selezionare una persona.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Persona p = contatti.get(riga);
            int conferma = JOptionPane.showConfirmDialog(
                view, 
                "Eliminare la persona " + p.getNome() + " " + p.getCognome() + "?", 
                "Conferma", 
                JOptionPane.YES_NO_OPTION
            );

            if (conferma == JOptionPane.YES_OPTION) {
                // Chiamata granulare al DB tramite ID
                boolean eliminato = dao.eliminaPersona(p.getId());
                if (eliminato) {
                    contatti.remove(riga);
                    view.aggiornaTabella(contatti);
                } else {
                    JOptionPane.showMessageDialog(view, "Errore durante l'eliminazione dal database.", "Errore DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Metodo privato per aprire l'editor di persona, sia per la creazione che per la modifica
    private void apriEditor(Persona p, int indice) {
        EditorPersonaDialog dialog = new EditorPersonaDialog(view, p == null ? "Nuova Persona" : "Modifica Persona");
        if (p != null) dialog.popolaCampi(p);

        dialog.getBtnAnnulla().addActionListener(e -> dialog.dispose());
        dialog.getBtnSalva().addActionListener(e -> {
            String nome = dialog.getNomeRaw();
            String cognome = dialog.getCognomeRaw();
            String indirizzo = dialog.getIndirizzoRaw();
            String telefono = dialog.getTelefonoRaw();
            String eta = dialog.getEtaRaw();

            // Validazione campi
            String errore = ValidatorePersona.valida(nome, cognome, indirizzo, telefono, eta);
            if (errore != null) {
                JOptionPane.showMessageDialog(dialog, errore, "Dati non validi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Controllo duplicati numero di telefono
            Persona personaConStessoNumero = null;
            for (int i = 0; i < contatti.size(); i++) {
                if (i != indice && contatti.get(i).getTelefono().equals(telefono)) {
                    personaConStessoNumero = contatti.get(i);
                    break;
                }
            }

            if (personaConStessoNumero != null) {
                int risposta = JOptionPane.showConfirmDialog(dialog,
                    "Il numero " + telefono + " è già associato a " 
                    + personaConStessoNumero.getNome() + " " + personaConStessoNumero.getCognome() 
                    + ".\nVuoi procedere comunque con il salvataggio?",
                    "Possibile Duplicato",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (risposta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Persona personaValidata = dialog.getDatiInseriti();

            if (indice == -1) {
                // Inserimento nuovo contatto nel DB
                boolean inserito = dao.aggiungiPersona(personaValidata);
                if (inserito) {
                    contatti.add(personaValidata); // Contiene già il nuovo ID generato dal DB
                    dialog.dispose();
                    view.aggiornaTabella(contatti);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Errore durante il salvataggio nel database.", "Errore DB", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Mantieni l'ID originale per eseguire la query WHERE id = ?
                personaValidata.setId(p.getId());

                // Aggiornamento contatto esistente nel DB
                boolean aggiornato = dao.aggiornaPersona(personaValidata);
                if (aggiornato) {
                    contatti.set(indice, personaValidata);
                    dialog.dispose();
                    view.aggiornaTabella(contatti);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Errore durante l'aggiornamento nel database.", "Errore DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        dialog.setVisible(true);
    }

    // Metodo privato per eseguire il logout dell'utente corrente
    private void eseguiLogout() {
        int conferma = JOptionPane.showConfirmDialog(
            view,
            "Sei sicuro di voler effettuare il logout?",
            "Conferma Uscita",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (conferma == JOptionPane.YES_OPTION) {
            rubrica.model.Sessione.logout();
            view.dispose();

            LoginDialog loginDialog = new LoginDialog();
            new rubrica.controller.LoginController(loginDialog, new rubrica.dao.impl.UtenteDAOMySQL(), () -> {
                FinestraPrincipale nuovaFinestra = new FinestraPrincipale();
                new rubrica.controller.RubricaController(nuovaFinestra, this.dao);
                nuovaFinestra.setVisible(true);
            });
            loginDialog.setVisible(true);
        }
    }
}