package rubrica.model;

// Classe che rappresenta un utente del sistema, con attributi come username e password
public class Utente {
    private int id; // ID univoco dell'utente nel database
    private String username;
    private String password;

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Utente(int id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}