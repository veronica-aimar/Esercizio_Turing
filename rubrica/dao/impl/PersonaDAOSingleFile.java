package rubrica.dao.impl;

import rubrica.dao.PersonaDAO;
import rubrica.model.Persona;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;

// Implementazione di PersonaDAO che salva tutte le persone in un unico file "informazioni.txt"
public class PersonaDAOSingleFile implements PersonaDAO {
    private static final String NOME_FILE = "informazioni.txt";

    // Caricamento di tutte le persone dal file "informazioni.txt" e restituzione come un vettore di oggetti Persona
    @Override
    public ArrayList<Persona> caricaPersone() {
        ArrayList<Persona> lista = new ArrayList<>();
        File file = new File(NOME_FILE);
        if (!file.exists()) return lista;

        // Lettura del file riga per riga, suddivisione dei dati separati da ";" e creazione di oggetti Persona
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] dati = linea.split(";");
                if (dati.length == 5) {
                    try {
                        lista.add(new Persona(dati[0].trim(), dati[1].trim(), dati[2].trim(), dati[3].trim(), Integer.parseInt(dati[4].trim())));
                    } catch (NumberFormatException e) { }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Salvataggio di tutte le persone nel file "informazioni.txt", sovrascrivendo il contenuto esistente
    @Override
    public void salvaPersone(ArrayList<Persona> persone) {
        try (PrintStream ps = new PrintStream(new File(NOME_FILE))) {
            for (Persona p : persone) {
                ps.println(p.formattaPerFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}