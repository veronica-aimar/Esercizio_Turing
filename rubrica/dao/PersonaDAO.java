package rubrica.dao;

import java.util.ArrayList;
import rubrica.model.Persona;

// Interfaccia per l'accesso ai dati delle persone
public interface PersonaDAO {
    ArrayList<Persona> caricaPersone();
    boolean aggiungiPersona(Persona p);
    boolean aggiornaPersona(Persona p);
    boolean eliminaPersona(int idPersona);
}