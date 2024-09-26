BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Bevanda" (
	"codice"	INTEGER,
	"nome"	TEXT NOT NULL,
	PRIMARY KEY("codice" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Cialde_per_Bevande" (
	"codice_cialda"	INTEGER,
	"codice_bevanda"	INTEGER,
	"n_cialde"	INTEGER NOT NULL CHECK("n_cialde" >= 0),
	PRIMARY KEY("codice_cialda","codice_bevanda"),
	FOREIGN KEY("codice_bevanda") REFERENCES "Bevanda"("codice") ON DELETE CASCADE,
	FOREIGN KEY("codice_cialda") REFERENCES "Consumabili"("id") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "Consumabili" (
	"id"	INTEGER,
	"nome"	TEXT NOT NULL,
	"n_disponibili"	INTEGER NOT NULL CHECK("n_disponibili" >= 0),
	"n_max"	INTEGER NOT NULL CHECK("n_max" >= 0),
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Macchinetta" (
	"id"	INTEGER,
	"id_scuola"	INTEGER,
	"piano"	INTEGER,
	"assenza_cialde"	BOOLEAN NOT NULL,
	"assenza_zucchero"	BOOLEAN NOT NULL,
	"assenza_bicchiere"	BOOLEAN NOT NULL,
	"assenza_resto"	BOOLEAN NOT NULL,
	"piena"	BOOLEAN NOT NULL,
	"guasto"	BOOLEAN NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("id_scuola") REFERENCES "Scuole"("id") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "Scuole" (
	"id"	INTEGER,
	"indirizzo"	TEXT NOT NULL,
	"nome"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "ricavi" (
	"id"	INTEGER,
	"id_macchinetta"	INTEGER,
	"denaro"	REAL,
	"data"	DATE,
	PRIMARY KEY("id"),
	FOREIGN KEY("id_macchinetta") REFERENCES "macchinette"("id")
);
CREATE TABLE IF NOT EXISTS "utenti" (
	"id"	INTEGER,
	"ruolo"	TEXT CHECK("ruolo" IN ('tecnici', 'amministratori', 'impiegati')),
	"codice"	TEXT UNIQUE,
	"pw"	TEXT,
	"email"	TEXT UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
COMMIT;
