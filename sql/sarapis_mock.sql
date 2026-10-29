-- Jeu de données factice pour la base "sarapis" (normalement en lecture seule,
-- peuplée par une synchro LDAP/ESCO externe - ici recréée à la main pour le
-- dev local). Reproduit le minimum de tables/colonnes lues par
-- SarapisQueryService.getClasses(uai), avec un seul établissement/classe de
-- test (UAI 0180000A, classe 6A1, niveau 6EME).

USE sarapis;

CREATE TABLE IF NOT EXISTS etablissement (
    id INT PRIMARY KEY,
    uai VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS agroupe (
    id INT PRIMARY KEY,
    cn VARCHAR(100) NOT NULL,
    categorie VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS agroupeofapersonne (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES agroupe(id)
);

CREATE TABLE IF NOT EXISTS agroupeoffoncclassegroupe (
    id INT PRIMARY KEY,
    etablissement_fk INT NOT NULL,
    FOREIGN KEY (id) REFERENCES agroupeofapersonne(id),
    FOREIGN KEY (etablissement_fk) REFERENCES etablissement(id)
);

CREATE TABLE IF NOT EXISTS classe (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES agroupeoffoncclassegroupe(id)
);

CREATE TABLE IF NOT EXISTS mef (
    id INT PRIMARY KEY,
    filiere VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS classes_mefs (
    CLASSE_ID INT NOT NULL,
    MEF_ID INT NOT NULL,
    FOREIGN KEY (CLASSE_ID) REFERENCES classe(id),
    FOREIGN KEY (MEF_ID) REFERENCES mef(id)
);

CREATE TABLE IF NOT EXISTS apersonne (
    id INT PRIMARY KEY,
    etat VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS apersonnes_agroupes (
    AGROUPEOFAPERS_ID INT NOT NULL,
    APERSONNE_ID INT NOT NULL,
    FOREIGN KEY (AGROUPEOFAPERS_ID) REFERENCES agroupeofapersonne(id),
    FOREIGN KEY (APERSONNE_ID) REFERENCES apersonne(id)
);

-- Seed : établissement 0180000A, classe 6A1 (niveau 6EME), 1 élève actif
-- INSERT IGNORE pour rester rejouable sans erreur si les données existent déjà.
INSERT IGNORE INTO etablissement (id, uai) VALUES (1, '0180000A');
INSERT IGNORE INTO agroupe (id, cn, categorie) VALUES (100, '6A1', 'Classe');
INSERT IGNORE INTO agroupeofapersonne (id) VALUES (100);
INSERT IGNORE INTO agroupeoffoncclassegroupe (id, etablissement_fk) VALUES (100, 1);
INSERT IGNORE INTO classe (id) VALUES (100);
INSERT IGNORE INTO mef (id, filiere) VALUES (1, '6EME');
INSERT IGNORE INTO classes_mefs (CLASSE_ID, MEF_ID) VALUES (100, 1);
INSERT IGNORE INTO apersonne (id, etat) VALUES (1, 'Actif');
INSERT IGNORE INTO apersonnes_agroupes (AGROUPEOFAPERS_ID, APERSONNE_ID) VALUES (100, 1);
