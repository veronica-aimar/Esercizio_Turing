package rubrica.dao.impl;

import rubrica.dao.DatabaseManager;
import rubrica.dao.UtenteDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Implementazione dell'interfaccia UtenteDAO per la gestione delle operazioni di persistenza dei dati degli utenti nel database MySQL
public class UtenteDAOMySQL implements UtenteDAO {
    // Verifica delle credenziali dell'utente nel database, restituendo true se esiste una corrispondenza
    @Override
    public boolean verificaCredenziali(String username, String password) {
        String query = "SELECT * FROM utenti WHERE username = ? AND password = ?";
        
        // Utilizzo di PreparedStatement per prevenire SQL injection e gestione sicura delle credenziali
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Ritorna true se trova una corrispondenza
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}