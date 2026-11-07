package rubrica.dao;

import rubrica.model.Utente;

// Interfaccia DAO per la gestione degli utenti, con un metodo per verificare le credenziali di accesso
public interface UtenteDAO {
    Utente autentica(String username, String password);
    boolean verificaCredenziali(String username, String password);
    
    // Metodi per la registrazione
    boolean registraUtente(String username, String password);
    boolean esisteUsername(String username);
}