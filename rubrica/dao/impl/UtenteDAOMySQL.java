package rubrica.dao.impl;

import rubrica.dao.DatabaseManager;
import rubrica.dao.UtenteDAO;
import rubrica.model.Utente;
import rubrica.util.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Implementazione dell'interfaccia UtenteDAO per la persistenza su MySQL con hashing sicuro nativo
public class UtenteDAOMySQL implements UtenteDAO {

    // Registra un nuovo utente salvando solo l'hash della password con salt
    @Override
    public boolean registraUtente(String username, String password) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            throw new IllegalStateException("Impossibile connettersi al database MySQL.\nAssicurati che il container Docker sia avviato.");
        }

        // Calcola l'hash "salt:hash" con PBKDF2
        String passwordHashata = PasswordHasher.calcolaHash(password);

        String query = "INSERT INTO utenti (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, passwordHashata);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la registrazione dell'utente: " + e.getMessage(), e);
        }
    }

    // Verifica le credenziali confrontando l'hash salvato con la password inserita
    @Override
    public boolean verificaCredenziali(String username, String password) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            throw new IllegalStateException("Impossibile connettersi al database MySQL.\nAssicurati che il container Docker sia avviato.");
        }

        String query = "SELECT password FROM utenti WHERE username = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passwordNelDb = rs.getString("password");
                    // Verifica tramite PBKDF2 (gestisce anche eventuali vecchie password in chiaro)
                    return PasswordHasher.verificaPassword(password, passwordNelDb);
                }
                return false; // Utente inesistente
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la verifica credenziali: " + e.getMessage(), e);
        }
    }

    // Controlla l'esistenza di un dato username
    @Override
    public boolean esisteUsername(String username) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            throw new IllegalStateException("Impossibile connettersi al database MySQL.\nAssicurati che il container Docker sia avviato.");
        }

        String query = "SELECT 1 FROM utenti WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la verifica dello username: " + e.getMessage(), e);
        }
    }

    // Autentica l'utente e restituisce un oggetto Utente se le credenziali sono corrette, altrimenti null
    @Override
    public Utente autentica(String username, String password) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            throw new IllegalStateException("Impossibile connettersi al database MySQL.\nAssicurati che il container Docker sia avviato.");
        }

        String query = "SELECT id, password FROM utenti WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashDb = rs.getString("password");
                    if (PasswordHasher.verificaPassword(password, hashDb)) {
                        int id = rs.getInt("id");
                        return new Utente(id, username);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'autenticazione: " + e.getMessage(), e);
        }
    }
}