package rubrica.dao.impl;

import rubrica.dao.DatabaseManager;
import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import rubrica.model.Sessione;

import java.sql.*;
import java.util.ArrayList;

public class PersonaDAOMySQL implements PersonaDAO {

    // Carica tutte le persone associate all'utente corrente
    @Override
    public ArrayList<Persona> caricaPersone() {
        ArrayList<Persona> contatti = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return contatti;
        }

        int utenteId = Sessione.getIdUtenteCorrente();
        // Includiamo l'id nel SELECT
        String query = "SELECT id, nome, cognome, indirizzo, telefono, eta FROM persone WHERE utente_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("indirizzo"),
                        rs.getString("telefono"),
                        rs.getInt("eta")
                    );
                    contatti.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contatti;
    }

    // Inserisce la persona nel database e assegna l'ID auto-generato all'oggetto Persona
    @Override
    public boolean aggiungiPersona(Persona p) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }

        int utenteId = Sessione.getIdUtenteCorrente();
        String query = "INSERT INTO persone (utente_id, nome, cognome, indirizzo, telefono, eta) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, utenteId);
            ps.setString(2, p.getNome());
            ps.setString(3, p.getCognome());
            ps.setString(4, p.getIndirizzo());
            ps.setString(5, p.getTelefono());
            ps.setInt(6, p.getEta());

            int righeInserite = ps.executeUpdate();
            if (righeInserite > 0) {
                // Recupera l'ID auto-generato dal database e lo assegna all'oggetto
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        p.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Aggiorna il contatto se l'utente lo modifica nella GUI
    @Override
    public boolean aggiornaPersona(Persona p) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }

        int utenteId = Sessione.getIdUtenteCorrente();
        String query = "UPDATE persone SET nome = ?, cognome = ?, indirizzo = ?, telefono = ?, eta = ? WHERE id = ? AND utente_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getIndirizzo());
            ps.setString(4, p.getTelefono());
            ps.setInt(5, p.getEta());
            ps.setInt(6, p.getId());
            ps.setInt(7, utenteId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Elimina solo il contatto rimosso
    @Override
    public boolean eliminaPersona(int idPersona) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }

        int utenteId = Sessione.getIdUtenteCorrente();
        String query = "DELETE FROM persone WHERE id = ? AND utente_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPersona);
            ps.setInt(2, utenteId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}