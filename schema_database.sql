CREATE DATABASE IF NOT EXISTS rubrica_db;
USE rubrica_db;

CREATE TABLE IF NOT EXISTS utenti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(300) NOT NULL
);

CREATE TABLE IF NOT EXISTS persone (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utente_id INT NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    indirizzo VARCHAR(150),
    telefono VARCHAR(20),
    eta INT,
    CONSTRAINT fk_persone_utenti FOREIGN KEY (utente_id) 
        REFERENCES utenti(id) 
        ON DELETE CASCADE
);

INSERT INTO utenti (id, username, password) VALUES (
    1,
    'admin',
    'admin'
);