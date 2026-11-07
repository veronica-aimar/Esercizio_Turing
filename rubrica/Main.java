package rubrica;

import rubrica.controller.LoginController;
import rubrica.controller.RubricaController;
import rubrica.dao.DatabaseManager;
import rubrica.dao.PersonaDAO;
import rubrica.dao.UtenteDAO;
import rubrica.dao.impl.PersonaDAOMySQL;
import rubrica.dao.impl.UtenteDAOMySQL;
import rubrica.view.FinestraPrincipale;
import rubrica.view.LoginDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inizializzazione automatica del database dallo script
        DatabaseManager.inizializzaDatabaseDaScript();

        // Avvio dell'applicazione in un thread separato per garantire la reattività dell'interfaccia utente
        SwingUtilities.invokeLater(() -> {
            PersonaDAO personaDAO = new PersonaDAOMySQL();
            UtenteDAO utenteDAO = new UtenteDAOMySQL();

            LoginDialog loginView = new LoginDialog();

            // Definizione di un Runnable che avvia l'applicazione principale dopo il login
            Runnable avviaApp = () -> {
                FinestraPrincipale vistaPrincipale = new FinestraPrincipale();
                new RubricaController(vistaPrincipale, personaDAO);
                vistaPrincipale.setVisible(true);
            };

            new LoginController(loginView, utenteDAO, avviaApp);
            loginView.setVisible(true);
        });

        // Garanzia della chiusura pulita della connessione alla chiusura del programma
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::closeConnection));
    }
}