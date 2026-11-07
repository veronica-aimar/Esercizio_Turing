package rubrica.dao.impl;

import rubrica.dao.DatabaseManager;
import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import java.sql.*;
import java.util.Vector;

// Implementazione dell'interfaccia PersonaDAO per la gestione delle operazioni di persistenza dei dati delle persone nella rubrica utilizzando un database MySQL
public class PersonaDAOMySQL implements PersonaDAO {
    // Carica tutte le persone dal database e le restituisce come un vettore di oggetti Persona
    @Override
    public Vector<Persona> caricaPersone() {
        Vector<Persona> lista = new Vector<>();
        String query = "SELECT * FROM persone";
        try (Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            
            // Itera attraverso il ResultSet e crea oggetti Persona per ogni riga del risultato
            while (rs.next()) {
                lista.add(new Persona(
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("indirizzo"),
                    rs.getString("telefono"),
                    rs.getInt("eta")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Salva tutte le persone nel database, svuotando prima la tabella e poi inserendo i nuovi dati
    @Override
    public void salvaPersone(Vector<Persona> persone) {
        // Per rispettare l'interfaccia che riceve l'intera lista, svuotamento e reinserimento
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.createStatement().executeUpdate("TRUNCATE TABLE persone"); // Svuota la tabella
            
            String insert = "INSERT INTO persone (nome, cognome, indirizzo, telefono, eta) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                for (Persona p : persone) {
                    ps.setString(1, p.getNome());
                    ps.setString(2, p.getCognome());
                    ps.setString(3, p.getIndirizzo());
                    ps.setString(4, p.getTelefono());
                    ps.setInt(5, p.getEta());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}