package rubrica.model;

// Rappresenta un contatto all'interno della rubrica
public class Persona {
    private int id; // Aggiunto per supportare l'identificatore univoco nel database
    private String nome;
    private String cognome;
    private String indirizzo;
    private String telefono;
    private int eta;

    public Persona(int id, String nome, String cognome, String indirizzo, String telefono, int eta) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.eta = eta;
    }

    // (L'ID viene impostato a 0, sarà il DAO a generare quello vero al momento del salvataggio)
    public Persona(String nome, String cognome, String indirizzo, String telefono, int eta) {
        this(0, nome, cognome, indirizzo, telefono, eta);
    }

    // Metodo utilizzato dai DAO su file per scrivere la riga formattata (separata da punti e virgola)
    public String formattaPerFile() {
        return id + ";" + nome + ";" + cognome + ";" + indirizzo + ";" + telefono + ";" + eta;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getEta() { return eta; }
    public void setEta(int eta) { this.eta = eta; }
}