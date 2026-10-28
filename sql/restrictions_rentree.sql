CREATE TABLE IF NOT EXISTS etablissement (
                                             id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             uai         VARCHAR(255) NOT NULL,
    dateRentree DATETIME NULL DEFAULT NULL,
    enabled     TINYINT(1) DEFAULT 1
    );

CREATE TABLE IF NOT EXISTS niveau (
                                      id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      etablissement_id BIGINT       NOT NULL,
                                      nom              VARCHAR(255) NOT NULL,
    dateRentree      DATETIME NULL,
    CONSTRAINT fk_niveau_etablissement FOREIGN KEY (etablissement_id) REFERENCES etablissement (id) ON DELETE CASCADE,
    CONSTRAINT uk_niveau UNIQUE (etablissement_id, nom)
    );

CREATE TABLE IF NOT EXISTS classe (
                                      id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      niveau_id   BIGINT       NOT NULL,
                                      nom         VARCHAR(255) NOT NULL,
    dateRentree DATETIME NULL,
    CONSTRAINT fk_classe_niveau FOREIGN KEY (niveau_id) REFERENCES niveau (id) ON DELETE CASCADE,
    CONSTRAINT uk_classe UNIQUE (niveau_id, nom)
    );

CREATE INDEX IF NOT EXISTS idx_niveau_etablissement ON niveau(etablissement_id);
CREATE INDEX IF NOT EXISTS idx_classe_niveau ON classe(niveau_id);