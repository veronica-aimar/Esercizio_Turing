package rubrica.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

// Utility per l'hashing sicuro delle password tramite PBKDF2 nativo in Java
public class PasswordHasher {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERAZIONI = 65536;
    private static final int LUNGHEZZA_CHIAVE = 256;
    private static final int DIMENSIONE_SALT = 16;

    // Genera l'hash con salt casuale. Restituisce una stringa "salt:hash" codificata in Base64
    public static String calcolaHash(String password) {
        try {
            // Generazione di un salt crittografico casuale
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[DIMENSIONE_SALT];
            random.nextBytes(salt);

            // Calcolo dell'hash con PBKDF2
            byte[] hash = calcolaPBKDF2(password.toCharArray(), salt);

            // Concatenazinoe di salt e hash separati da ":" in Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Errore durante l'hashing della password: " + e.getMessage(), e);
        }
    }

    // Verifica se la password inserita corrisponde alla stringa "salt:hash" salvata nel DB
    public static boolean verificaPassword(String passwordInserita, String recordDb) {
        if (recordDb == null || !recordDb.contains(":")) {
            // Compatibilità transitoria: se nel DB c'era una vecchia password in chiaro, la confronta
            return passwordInserita.equals(recordDb);
        }

        try {
            String[] parti = recordDb.split(":");
            byte[] salt = Base64.getDecoder().decode(parti[0]);
            byte[] hashAtteso = Base64.getDecoder().decode(parti[1]);

            byte[] hashCalcolato = calcolaPBKDF2(passwordInserita.toCharArray(), salt);

            // Confronto a tempo costante per prevenire timing attacks
            return slowEquals(hashAtteso, hashCalcolato);

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] calcolaPBKDF2(char[] password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERAZIONI, LUNGHEZZA_CHIAVE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
        return factory.generateSecret(spec).getEncoded();
    }

    // Confronto byte-per-byte a tempo costante
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}