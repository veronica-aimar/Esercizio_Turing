package rubrica.controller;

import rubrica.model.Utente;
import rubrica.model.Sessione;
import rubrica.dao.UtenteDAO;
import rubrica.view.LoginDialog;
import javax.swing.JOptionPane;

// Controller per gestire login e registrazione utente
public class LoginController {
    private LoginDialog loginView;
    private UtenteDAO utenteDAO;
    private Runnable onSuccess;

    public LoginController(LoginDialog loginView, UtenteDAO utenteDAO, Runnable onSuccess) {
        this.loginView = loginView;
        this.utenteDAO = utenteDAO;
        this.onSuccess = onSuccess;

        // Il pulsante principale gestisce sia Login sia Registrazione in base alla modalità attiva
        loginView.getBtnAzione().addActionListener(e -> gestisciAzione());
    }

    // Gestisce l'azione del pulsante principale in base alla modalità attiva (Login o Registrazione)
    private void gestisciAzione() {
        if (loginView.isModalitaRegistrazione()) {
            eseguiRegistrazione();
        } else {
            eseguiLogin();
        }
    }

    // Esegue il login dell'utente verificando le credenziali tramite UtenteDAO
    private void eseguiLogin() {
        String user = loginView.getUsername();
        String pass = loginView.getPassword();

        // Controllo se i campi username e password sono vuoti
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Inserisci username e password.", "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Autentica l'utente tramite UtenteDAO
            Utente utente = utenteDAO.autentica(user, pass);

            if (utente != null) {
                // Imposta l'utente loggato nella sessione
                Sessione.setUtenteCorrente(utente);
                
                loginView.dispose();
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(loginView, "Username o password errati. Riprova.", "Login non riuscito", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(loginView, e.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(loginView, "Si è verificato un errore: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Esegue la registrazione dell'utente verificando i dati e salvandoli tramite UtenteDAO
    private void eseguiRegistrazione() {
        String user = loginView.getUsername();
        String pass = loginView.getPassword();
        String confermaPass = loginView.getConfermaPassword();

        // Controllo se i campi sono vuoti
        if (user.isEmpty() || pass.isEmpty() || confermaPass.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginView,
                "Tutti i campi sono obbligatori.",
                "Dati mancanti",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Controllo se le due password coincidono
        if (!pass.equals(confermaPass)) {
            JOptionPane.showMessageDialog(
                loginView,
                "Le due password non coincidono.",
                "Errore Password",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            // Controllo se lo username è già presente nel database e in caso positivo avviso l'utente
            if (utenteDAO.esisteUsername(user)) {
                JOptionPane.showMessageDialog(
                    loginView,
                    "Lo username '" + user + "' è già utilizzato. Scegline un altro.",
                    "Username non disponibile",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Salvataggio dell'utente tramite UtenteDAO
            if (utenteDAO.registraUtente(user, pass)) {
                JOptionPane.showMessageDialog(
                    loginView,
                    "Registrazione completata con successo!\nOra puoi effettuare l'accesso.",
                    "Account Creato",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loginView.alternaModalita(); // Riporta la schermata in modalità Login pronta all'accesso
            } else {
                JOptionPane.showMessageDialog(
                    loginView,
                    "Impossibile creare l'account.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(
                loginView,
                e.getMessage(),
                "Errore Database",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                loginView,
                "Si è verificato un errore durante la registrazione: " + e.getMessage(),
                "Errore",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}