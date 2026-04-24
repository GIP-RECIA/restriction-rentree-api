```sql
CREATE TABLE etablissement
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    uai         VARCHAR(255) NOT NULL,
    dateRentree TIMESTAMP    NULL DEFAULT NULL
    enabled     tinyint(1)   DEFAULT 1;
);

CREATE TABLE niveau
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    etablissement_id BIGINT       NOT NULL,
    nom              VARCHAR(255) NOT NULL,
    dateRentree      TIMESTAMP NULL,
    CONSTRAINT fk_niveau_etablissement FOREIGN KEY (etablissement_id) REFERENCES etablissement (id) ON DELETE CASCADE,
    CONSTRAINT uk_niveau UNIQUE (etablissement_id, nom)
);

CREATE TABLE classe
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    niveau_id   BIGINT       NOT NULL,
    nom         VARCHAR(255) NOT NULL,
    dateRentree TIMESTAMP NULL,
    CONSTRAINT fk_classe_niveau FOREIGN KEY (niveau_id) REFERENCES niveau (id) ON DELETE CASCADE,
    CONSTRAINT uk_classe UNIQUE (niveau_id, nom)
);

CREATE INDEX idx_niveau_etablissement ON niveau(etablissement_id);
CREATE INDEX idx_classe_niveau ON classe(niveau_id);
```