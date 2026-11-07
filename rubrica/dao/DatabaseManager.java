package rubrica.dao;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

// Classe che gestisce la connessione al database MySQL e l'inizializzazione automatica dello schema se necessario
public class DatabaseManager {
    private static Connection connection = null;

    // Esecuzione automatica dello script schema_database.sql
    public static void inizializzaDatabaseDaScript() {
        Properties props = caricaProprieta();
        if (props == null) return;

        String ip = props.getProperty("ip-server-mysql");
        String porta = props.getProperty("porta");
        String user = props.getProperty("username");
        String pass = props.getProperty("password");

        String urlBase = "jdbc:mysql://" + ip + ":" + porta + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        // Caricamento del driver JDBC e connessione al server MySQL per verificare l'esistenza del database rubrica_db
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connBase = DriverManager.getConnection(urlBase, user, pass);
                Statement stmt = connBase.createStatement()) {

                // Controlla se il database rubrica_db esiste già
                try (var rs = stmt.executeQuery("SHOW DATABASES LIKE 'rubrica_db'")) {
                    if (rs.next()) {
                        // Il DB esiste già: non serve eseguire nulla
                        return;
                    }
                }

                // Se non esiste, legge ed esegue schema_database.sql
                File fileSql = new File("schema_database.sql");
                if (!fileSql.exists()) {
                    try {
                        File dirJar = new File(DatabaseManager.class.getProtectionDomain()
                                .getCodeSource().getLocation().toURI()).getParentFile();
                        fileSql = new File(dirJar, "schema_database.sql");
                    } catch (Exception ignored) {}
                }

                try (Scanner scanner = new Scanner(fileSql)) {
                    scanner.useDelimiter(";");
                    while (scanner.hasNext()) {
                        String query = scanner.next().trim();
                        if (!query.isEmpty() && !query.startsWith("--")) {
                            try {
                                stmt.execute(query);
                            } catch (Exception ex) {
                                // Ignora eventuali eccezioni su singole istruzioni non bloccanti
                            }
                        }
                    }
                    System.out.println("Primo avvio: Database 'rubrica_db' creato e inizializzato con successo!");
                }
            }
        } catch (Exception e) {
            System.err.println("Errore durante la verifica/inizializzazione del database: " + e.getMessage());
        }
    }

    // Restituzione della connessione attiva verso rubrica_db riutilizzandola
    public static Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            // Caricamento delle proprietà dal file credenziali_database.properties
            Properties props = caricaProprieta();
            if (props == null) return null;

            String ip = props.getProperty("ip-server-mysql");
            String porta = props.getProperty("porta");
            String user = props.getProperty("username");
            String pass = props.getProperty("password");

            // Caricamento del driver JDBC e apertura della connessione al database rubrica_db
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + ip + ":" + porta + "/rubrica_db"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC"
                    + "&connectTimeout=3000";

            connection = DriverManager.getConnection(url, user, pass);
            return connection;
        } catch (Exception e) {
            System.err.println("Errore di connessione al database: " + e.getMessage());
            return null;
        }
    }

    // Chiusura della connessione al database se aperta
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione al database chiusa correttamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Caricamento delle proprietà dal file credenziali_database.properties
    private static Properties caricaProprieta() {
        Properties props = new Properties();
        
        // Prova prima il percorso relativo standard
        File fileProps = new File("credenziali_database.properties");

        // Se non lo trova, cerca nella cartella reale in cui risiede il .jar o la classe
        if (!fileProps.exists()) {
            try {
                File dirJar = new File(DatabaseManager.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI()).getParentFile();
                fileProps = new File(dirJar, "credenziali_database.properties");
            } catch (Exception ignored) {}
        }

        try (FileInputStream in = new FileInputStream(fileProps)) {
            props.load(in);
            return props;
        } catch (Exception e) {
            System.err.println("Impossibile leggere credenziali_database.properties: " + fileProps.getAbsolutePath() + " (" + e.getMessage() + ")");
            return null;
        }
    }
}