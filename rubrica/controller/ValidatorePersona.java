package rubrica.controller;

// Classe ValidatorePersona che fornisce metodi statici per validare i campi di una persona, come nome, cognome, indirizzo, telefono ed età
public class ValidatorePersona {

    // Regex per nomi e cognomi: lettere (anche accentate), spazi, apostrofi e trattini
    private static final String REGEX_TESTO = "^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s-]+$";
    
    // Regex per telefono: ammette eventuale prefisso '+', numeri, spazi e trattini
    // Lunghezza minima 3 (es. 112, 118), massima 20 caratteri
    private static final String REGEX_TELEFONO = "^\\+?[0-9\\s-]{3,20}$";

    /* Validazione dei campi inseriti.
    Restituzione di null se tutti i campi sono validi, altrimenti restituzione del messaggio di errore da mostrare all'utente */
    public static String valida(String nome, String cognome, String indirizzo, String telefono, String etaStr) {
        // Controllo carattere riservato di persistenza
        if (contienePuntoEVirgola(nome, cognome, indirizzo, telefono)) {
            return "I campi non possono contenere il carattere ';' (riservato per il salvataggio).";
        }

        // Controllo Nome (campo obbligatorio)
        if (nome == null || nome.trim().isEmpty()) {
            return "Il campo 'Nome' è obbligatorio.";
        }
        if (nome.trim().length() < 2 || nome.trim().length() > 50 || !nome.matches(REGEX_TESTO)) {
            return "Il 'Nome' deve contenere tra 2 e 50 caratteri alfabetici validi.";
        }

        // Controllo Cognome (campo obbligatorio)
        if (cognome == null || cognome.trim().isEmpty()) {
            return "Il campo 'Cognome' è obbligatorio.";
        }
        if (cognome.trim().length() < 2 || cognome.trim().length() > 50 || !cognome.matches(REGEX_TESTO)) {
            return "Il 'Cognome' deve contenere tra 2 e 50 caratteri alfabetici validi.";
        }

        // Controllo Telefono (campo obbligatorio)
        if (telefono == null || telefono.trim().isEmpty()) {
            return "Il campo 'Telefono' è obbligatorio per una rubrica telefonica.";
        }
        String telPulito = telefono.trim();
        if (telPulito.length() > 20) {
            return "Il numero di telefono non può superare i 20 caratteri.";
        }
        if (!telefono.trim().matches(REGEX_TELEFONO)) {
            return "Il formato del telefono non è valido. Usare solo numeri ed eventuale prefisso (es. +39 0612344).";
        }

        // Controllo Indirizzo (campo opzionale)
        if (indirizzo != null && !indirizzo.trim().isEmpty()) {
            if (indirizzo.trim().length() > 150) {
                return "L'indirizzo non può superare i 150 caratteri.";
            }
        }

        // Controllo Età (campo opzionale: se vuota consentita, se compilata deve essere coerente)
        if (etaStr != null && !etaStr.trim().isEmpty()) {
            try {
                int eta = Integer.parseInt(etaStr.trim());
                if (eta < 1 || eta > 125) {
                    return "L'età deve essere compresa tra 1 e 125 anni.";
                }
            } catch (NumberFormatException e) {
                return "L'età deve essere un numero intero valido.";
            }
        }

        return null; // Nessun errore rilevato
    }

    // Metodo privato per verificare se uno qualsiasi dei campi contiene il carattere ';'
    private static boolean contienePuntoEVirgola(String... testi) {
        for (String t : testi) {
            if (t != null && t.contains(";")) {
                return true;
            }
        }
        return false;
    }
}