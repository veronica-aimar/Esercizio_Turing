package rubrica;

import rubrica.controller.LoginController;
import rubrica.controller.RubricaController;
import rubrica.dao.DatabaseManager;
import rubrica.dao.PersonaDAO;
import rubrica.dao.UtenteDAO;
import rubrica.dao.impl.PersonaDAOMultiFile;
import rubrica.dao.impl.PersonaDAOMySQL;
import rubrica.dao.impl.PersonaDAOSingleFile;
import rubrica.dao.impl.UtenteDAOMySQL;
import rubrica.view.FinestraPrincipale;
import rubrica.view.LoginDialog;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    // VARIABILE PER TESTARE LE DIVERSE IMPLEMENTAZIONI:
    // "MYSQL", "SINGLEFILE" oppure "MULTIFILE"
    private static final String TIPO_SALVATAGGIO = "MYSQL"; 

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inizializza il database (serve comunque per il login)
        DatabaseManager.inizializzaDatabaseDaScript();

        SwingUtilities.invokeLater(() -> {
            
            // Scelta dinamica dell'implementazione di PersonaDAO tramite Polimorfismo
            PersonaDAO personaDAO;
            switch (TIPO_SALVATAGGIO) {
                case "SINGLEFILE":
                    personaDAO = new PersonaDAOSingleFile();
                    break;
                case "MULTIFILE":
                    personaDAO = new PersonaDAOMultiFile();
                    break;
                case "MYSQL":
                default:
                    personaDAO = new PersonaDAOMySQL();
                    break;
            }

            // Il login usa SEMPRE MySQL
            UtenteDAO utenteDAO = new UtenteDAOMySQL();

            LoginDialog loginView = new LoginDialog();

            Runnable avviaApp = () -> {
                FinestraPrincipale vistaPrincipale = new FinestraPrincipale();
                // Il controller riceve l'implementazione scelta sopra
                new RubricaController(vistaPrincipale, personaDAO);
                vistaPrincipale.setVisible(true);
            };

            new LoginController(loginView, utenteDAO, avviaApp);
            loginView.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::closeConnection));
    }
}