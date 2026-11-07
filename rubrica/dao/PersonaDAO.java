package rubrica.dao;

import rubrica.model.Persona;
import java.util.Vector;

// Interfaccia DAO per la gestione delle operazioni di persistenza dei dati delle persone nella rubrica
public interface PersonaDAO {
    Vector<Persona> caricaPersone();
    void salvaPersone(Vector<Persona> persone);
}