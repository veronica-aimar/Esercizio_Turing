# Rubrica Gestionale Java Swing

Applicazione desktop per la gestione di una rubrica telefonica sviluppata in **Java (Swing)**, progettata seguendo i pattern architetturali **MVC (Model-View-Controller)** e **DAO (Data Access Object)**, e dotata di un'interfaccia grafica moderna e accessibile.

Il sistema include l'autenticazione multi-utente, il supporto a tre diverse strategie di persistenza (file singolo, file multipli per contatto e database relazionale MySQL con inizializzazione automatica dello schema), validazione dei dati e gestione delle scorciatoie da tastiera.

---

## Architettura del Progetto

Il sistema separa le responsabilità con un'articolazione su tre livelli:

```
┌────────────────────────────────────────────────────────┐
│                        VIEW                            │
│  FinestraPrincipale, EditorPersonaDialog, LoginDialog  │
│  BottoneModerno, Icone                                 │
└───────────────────────▲────────────────────────────────┘
                        │ Eventi / Binding
┌───────────────────────▼────────────────────────────────┐
│                     CONTROLLER                         │
│  RubricaController, LoginController, ValidatorePersona │
└───────────────────────▲────────────────────────────────┘
                        │ Lettura / Aggiornamento
┌───────────────────────▼────────────────────────────────┐
│                       MODEL                            │
│  Persona, Utente                                       │
└───────────────────────▲────────────────────────────────┘
                        │ Mapping
┌───────────────────────▼────────────────────────────────┐
│                        DAO                             │
│  PersonaDAO (Interfaccia) ──► SingleFile / Multi / DB  │
│  UtenteDAO  (Interfaccia) ──► UtenteDAOMySQL           │
│  DatabaseManager (Connessioni & Init Schema)           │
└────────────────────────────────────────────────────────┘

```

---

## Scelte Progettuali e Motivazioni Tecniche

### 1. Pattern MVC e Disaccoppiamento DAO

* **Scelta:** Separare la presentazione (`view`), la logica di business (`controller`) e le strutture dati (`model`), interfacciando la persistenza solo tramite contratti astratti (`PersonaDAO`, `UtenteDAO`).
* **Motivazione:** Evita l'accoppiamento tipico delle applicazioni Swing monolitiche in cui le query SQL o la scrittura su file sono innestate negli `ActionListener` dei bottoni. In questo modo è possibile cambiare il motore di persistenza o la libreria grafica senza toccare la logica applicativa.

### 2. Persistenza Polimorfa (SingleFile, MultiFile, MySQL)

Il software supporta tre implementazioni intercambiabili di `PersonaDAO`:

1. **`PersonaDAOSingleFile`:** Salva l'intera rubrica nel file `informazioni.txt`.


2. **`PersonaDAOMultiFile`:** Salva ogni persona in un file dedicato all'interno della cartella `informazioni/`.
* *Motivazione:* Soddisfa requisiti di persistenza granulare e partizionata a livello di file system. Le omonimie vengono gestite con suffissi progressivi (`Mario-Rossi.txt`, `Mario-Rossi_1.txt`), evitando sovrascritture accidentali.


3. **`PersonaDAOMySQL`:** Persistenza su database relazionale tramite JDBC.
* *Motivazione:* Offre persistenza transazionale ACID, indicizzazione efficiente, query puntuali (`INSERT`, `UPDATE`, `DELETE`) e concorrenza multi-utente. L'inizializzazione automatica dello schema DB (vedi punto successivo) riduce il setup manuale richiesto all'utente.



### 3. Inizializzazione Automatica dello Schema DB (Zero-Configuration)

* **Scelta:** All'avvio, `DatabaseManager.inizializzaDatabaseDaScript()` si connette all'endpoint generico del server MySQL (`jdbc:mysql://ip:porta/?...`), verifica se `rubrica_db` esiste già e, in caso negativo, esegue in sequenza i comandi DDL/DML contenuti in `schema_database.sql`.
* **Motivazione:** Elimina l'onere per l'utente di dover aprire MySQL Workbench per lanciare manualmente gli script di creazione del database. Il controllo preliminare `SHOW DATABASES LIKE 'rubrica_db'` previene la reinizializzazione a ogni riavvio, proteggendo i record già inseriti da eventuali istruzioni distruttive (`DROP/TRUNCATE`) e velocizzando il boot dell'app.

### 4. Gestione del Ciclo di Vita delle Connessioni JDBC

* **Scelta:** Adozione del pattern **Singleton / Connessione Riutilizzata** in `DatabaseManager` combinato con uno **Shutdown Hook** della JVM.
* **Motivazione:** Aprire e chiudere una connessione socket TCP a ogni singola query genera un collo di bottiglia dovuto all'handshake SSL e all'autenticazione MySQL. Mantenendo la connessione aperta e chiudendo i soli `Statement` e `ResultSet` tramite *try-with-resources*, l'applicazione rimane scattante. Lo Shutdown Hook:
```java
Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::closeConnection));

```
garantisce il rilascio formale delle risorse di rete alla chiusura del processo.

### 5. Interfaccia Grafica: Risoluzione Criticità Native di Swing

* **Scelta:** Creazione del componente personalizzato `BottoneModerno` (estensione di `JButton`) con rendering custom via `paintComponent`.
* **Motivazione:** In un ambiente Windows il Look & Feel di sistema (`WindowsLookAndFeel`) forza rendering nativi che ignorano i colori `setBackground()` o producono testi sbiaditi con scarso contrasto. `BottoneModerno` risolve il problema alla radice con:
* Antialiasing attivo sui testi e sui bordi arrotondati;
* Stati cromatici espliciti: colore base, colore hover al passaggio del mouse e colore al clic;
* Rispetto delle linee guida di accessibilità WCAG sul contrasto visivo.



### 6. Iconografia Vettoriale via Codice (Java2D)

* **Scelta:** Implementazione della classe `Icone` che disegna programmaticamente le icone vettoriali (`+`, matita, cestino, disco/spunta, `X`) tramite primitive `Graphics2D`.
* **Motivazione:** Le icone in Java2D sono:
* Totalmente auto-contenute (nessuna dipendenza da risorse esterne);
* Vettoriali e scalabili senza perdita di risoluzione;
* Coerenti con la palette cromatica dell'interfaccia.



### 7. Ergonomia e Disposizione delle Barre degli Strumenti

* **Finestra Principale:** La `JToolBar` è posizionata in alto (`BorderLayout.NORTH`).
* **Finestra di Dialogo (Editor Persona):** I bottoni di azione (*Salva*, *Annulla*) sono collocati in basso a destra (`BorderLayout.SOUTH`). Nei form modali l'occhio dell'utente esegue una scansione verticale dall'alto in basso; posizionare le conferme a fine modulo evita errori di input.

### 8. Scorciatoie da Tastiera (Key Bindings)

* **Scelta:** Integrazione dei tasti di scelta rapida tramite `getRootPane().setDefaultButton()` e la combinazione `InputMap` / `ActionMap` a livello di `WHEN_IN_FOCUSED_WINDOW`.
* **Comportamenti mappati:**
* **Login:** `Invio` esegue l'autenticazione;
* **Finestra Principale:** `Invio` apre la modifica del contatto selezionato; `Canc` avvia il flusso di eliminazione con richiesta di conferma.
* **Editor Contatto:** `Invio` salva la scheda; `Canc` o `Esc` chiudono il dialogo annullando le modifiche.


* **Motivazione:** Consente l'utilizzo a utenti avanzati senza dover ricorrere all'uso del mouse.

### 9. Strategia di Validazione e Integrità dei Dati

La logica è incapsulata nella classe statica `ValidatorePersona`:

| Campo | Regola di Business | Rationale Tecnico |
| --- | --- | --- |
| **Nome & Cognome** | Obbligatorio (2–50 caratteri alfabetici) | Costituiscono l'identificativo primario e sono indispensabili per ordinamenti e visualizzazione. |
| **Telefono** | Obbligatorio (`^\+?[0-9\s-]{3,20}$`) | Trattandosi di una rubrica, il recapito è essenziale. Il range 3–20 ammette numeri brevi (es. `112`), formati internazionali e rispetta il limite `VARCHAR(20)` del database evitando eccezioni di Data truncation. |
| **Età** | Opzionale (se inserita: $1 \le \text{età} \le 125$) | Dato accessorio. Se omessa viene salvata come `0` e la view la visualizza come campo vuoto anziché stampare lo `0` numerico. |
| **Indirizzo** | Opzionale ($\le 150$ caratteri) | Informazione secondaria di recapito. |
| **Filtro Punto e Virgola** | Blocco categorico del carattere `;` | Il carattere `;` è il delimitatore dei record su file `.txt`. Ammetterlo corromperebbe la parsificazione con `split(";")`. |

* **Numeri Telefonici Duplicati:** Il sistema non impone un blocco distruttivo ma notifica un avviso con richiesta di conferma, consentendo la condivisione di un numero tra colleghi di lavoro o conviventi.

### 10. Gestione File System: Sanificazione e Omonimie

Nella persistenza multi-file (`PersonaDAOMultiFile`):

* **Sanificazione nomi file:** I sistemi operativi (in particolare Windows) vietano caratteri speciali (`\ / : * ? " < > |`) e nomi riservati (`CON`, `PRN`, `NUL`). Il metodo `sanificaNomeFile` normalizza le stringhe e rimuove spazi o punti finali.
* **Risoluzione Omonimie:** Due contatti con lo stesso nome e cognome causerebbero la sovrascrittura dello stesso file `Nome-Cognome.txt`. Il DAO indicizza le collisioni generando suffissi progressivi (`Mario-Rossi.txt`, `Mario-Rossi_1.txt`), preservando l'integrità dell'archivio.

---

## Struttura dei Package e File

* Controller: Gestione autenticazione e transizione viste;
* DAO: Persistenza dei dati e gestione connessione JDBC;
* Model: Entità `Persona` e `Utente`;

```text
rubrica-project/
├── bin/
├── informazioni/
├── lib/
├── rubrica/
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── RubricaController.java
│   │   └── ValidatorePersona.java
│   ├── dao/
│   │   ├── impl/
│   │   │   ├── PersonaDAOMultiFile.java
│   │   │   ├── PersonaDAOMySQL.java
│   │   │   ├── PersonaDAOSingleFile.java
│   │   │   └── UtenteDAOMySQL.java
│   │   ├── DatabaseManager.java
│   │   ├── PersonaDAO.java
│   │   └── UtenteDAO.java
│   ├── model/
│   │   ├── Persona.java
│   │   └── Utente.java
│   ├── view/
│   │   ├── BottoneModerno.java
│   │   ├── EditorPersonaDialog.java
│   │   ├── FinestraPrincipale.java
│   │   ├── Icone.java
│   │   └── LoginDialog.java
│   └── Main.java
├── credenziali_database.properties
├── docker-compose.yml
├── manifest.mf
├── Rubrica.jar
└── schema_database.sql
```

---

## Requisiti e Guida all'Avvio

### Requisiti di Sistema

* **Java Development Kit (JDK):** Versione 17 o superiore;
* **Server Database:** MySQL Server (locale o remoto) attivo;
* **Driver JDBC:** `mysql-connector-j-x.x.x.jar` configurato nel classpath.

### Configurazione

1. Clonare o estrarre la cartella del progetto.
2. Copiare `credenziali_database.properties.example` in
    `credenziali_database.properties` nella radice del progetto e valorizzarlo
    con i propri parametri:
```properties
ip-server-mysql=localhost
porta=3307
username=root
password=root
```

3. Assicurarsi che il file `schema_database.sql` sia presente nella cartella principale.
4. Inserire un connettore JDBC compatibile con MySQL nella cartella `lib/` (es. `mysql-connector-j-8.0.33.jar`).

Il file `credenziali_database.properties` e i JAR contenuti in `lib/` non
devono essere pubblicati su GitHub: il primo può contenere password, mentre il
driver JDBC è un artefatto esterno. Le cartelle `lib/` e `informazioni/` sono
presenti nel repository tramite i rispettivi file README.

### Avvio con Docker

Docker avvia MySQL con `docker compose up -d` e monta automaticamente
`schema_database.sql` al primo avvio del container. L’applicazione Java deve comunque leggere `credenziali_database.properties`: Docker non configura automaticamente questo file.

Con il `docker-compose.yml` incluso nel progetto, i valori da usare sono:

```properties
ip-server-mysql=localhost
porta=3307
username=root
password=root
```

La porta `3307` è quella esposta da Docker sul computer; `3306` è la porta interna del container. Il file `schema_database.sql` non deve essere eseguito manualmente, ma deve restare presente perché Docker lo monta nel container.

Per il primo avvio:

```powershell
docker compose up -d
```

Il volume Docker `mysql_data` conserva i dati. Di conseguenza, lo script di inizializzazione viene eseguito solo quando il volume è vuoto. Per ricreare il database da zero, eliminare anche il volume con `docker compose down -v`.

### Esecuzione

Compilare ed eseguire la classe `rubrica.Main`.
Oppure fare doppio click sul file `Rubrica.jar` generato con lo script di build.

All'avvio, l'applicazione:

1. Rileva automaticamente la presenza del database e delle tabelle, inizializzandoli se necessario;
2. Presenta la finestra di login. È possibile accedere con le credenziali predefinite presenti nello schema (`admin` / `admin`);
3. Apre la gestione contatti completa.


### Creazione del file JAR

Il progetto contiene già il task di compilazione `Build Rubrica.jar` in
`.vscode/tasks.json`. Per generare il JAR da VS Code:

1. Aprire la cartella principale del progetto in VS Code.
2. Premere `Ctrl+Maiusc+B`.
3. Se richiesto, selezionare **Build Rubrica.jar**.

Il file `Rubrica.jar` viene creato nella cartella principale del progetto. È
un artefatto generato e non deve essere aggiunto a GitHub.

In alternativa, aprire un terminale PowerShell nella cartella principale del progetto ed eseguire:

```powershell
$sorgenti = Get-ChildItem -Path rubrica -Recurse -Filter *.java |
    ForEach-Object FullName

Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force bin | Out-Null
javac -cp "lib\*" -d bin $sorgenti
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
& "$env:JAVA_HOME\bin\jar.exe" cfm Rubrica.jar manifest.mf -C bin .
```

Il comando richiede un JDK installato, perché utilizza `javac` e `jar.exe`.
Se Java è installato in una posizione diversa, modificare il valore di
`JAVA_HOME` di conseguenza.

Per avviare l'applicazione con doppio click, mantenere nella stessa cartella di `Rubrica.jar`:

* la cartella `lib/`, contenente il driver MySQL;
* `credenziali_database.properties`;
* `schema_database.sql`;
* la cartella `informazioni/`, se si utilizza la persistenza su file.

È inoltre necessario avere Java installato e associato ai file `.jar` in
Windows.

Per eseguirlo dal terminale:

```powershell
java -jar Rubrica.jar
```