CREATE DATABASE IF NOT EXISTS rubrica_db;
USE rubrica_db;

CREATE TABLE IF NOT EXISTS utenti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS persone (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    indirizzo VARCHAR(150),
    telefono VARCHAR(20),
    eta INT
);

-- Inserimento di un utente di default per poter fare il primo login
INSERT INTO utenti (username, password) VALUES ('admin', 'admin');