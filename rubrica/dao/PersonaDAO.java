package rubrica.dao;

import rubrica.model.Persona;
import java.util.ArrayList;

// Interfaccia DAO per la gestione delle operazioni di persistenza dei dati delle persone nella rubrica
public interface PersonaDAO {
    ArrayList<Persona> caricaPersone();
    void salvaPersone(ArrayList<Persona> persone);
}