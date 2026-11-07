package rubrica.model;

// Memorizza l'utente attualmente autenticato per tutta la sessione dell'applicazione
public class Sessione {
    private static Utente utenteCorrente;

    public static void setUtenteCorrente(Utente utente) {
        utenteCorrente = utente;
    }

    public static Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public static int getIdUtenteCorrente() {
        return utenteCorrente != null ? utenteCorrente.getId() : -1;
    }

    public static void logout() {
        utenteCorrente = null;
    }
}