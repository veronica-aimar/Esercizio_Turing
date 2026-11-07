package rubrica.dao;

// Interfaccia DAO per la gestione degli utenti, con un metodo per verificare le credenziali di accesso
public interface UtenteDAO {
    boolean verificaCredenziali(String username, String password);
}