package rubrica.controller;

import rubrica.dao.UtenteDAO;
import rubrica.view.LoginDialog;
import javax.swing.JOptionPane;

// Controller per gestire il processo di login, interagendo con la vista LoginDialog e il DAO UtenteDAO per verificare le credenziali dell'utente
public class LoginController {
    private LoginDialog loginView;
    private UtenteDAO utenteDAO;
    private Runnable onSuccess;

    /* Costruttore del controller di login
        @param loginView La vista del login
        @param utenteDAO Il DAO per la gestione degli utenti
        @param onSuccess La funzione da eseguire in caso di login riuscito */
    public LoginController(LoginDialog loginView, UtenteDAO utenteDAO, Runnable onSuccess) {
        this.loginView = loginView;
        this.utenteDAO = utenteDAO;
        this.onSuccess = onSuccess;

        loginView.getBtnLogin().addActionListener(e -> eseguiLogin());
    }

    // Metodo privato per eseguire il login, verificando le credenziali e gestendo la risposta della vista
    private void eseguiLogin() {
        String user = loginView.getUsername();
        String pass = loginView.getPassword();

        // Verifica delle credenziali tramite il DAO UtenteDAO
        if (utenteDAO.verificaCredenziali(user, pass)) {
            loginView.dispose(); // Chiusura della finestra di login
            onSuccess.run(); // Avvio della finestra principale
        } else {
            // Messaggio di errore se le credenziali sono errate
            JOptionPane.showMessageDialog(loginView, "Login errato. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}