.PHONY: help db db-init db-down db-clean wait-db init-project run build test clean

DB_CONTAINER := restriction-rentree-db
DB_IMAGE := mariadb:11

help:
	@echo "make db	  - lance MariaDB en local (conteneur Docker, port 3306)"
	@echo "make db-init   - crée le schema restrictions_rentree (depuis DB.md) + la base sarapis vide"
	@echo "make db-down   - arrete le conteneur (les donnees sont gardees)"
	@echo "make db-clean  - arrete et supprime le conteneur + ses donnees"
	@echo "make build     - compile le projet (./mvnw clean package)"
	@echo "make run	 - lance l'application (./mvnw spring-boot:run)"
	@echo "make test	- lance les tests unitaires"
	@echo ""
	@echo "Ordre pour repartir de zero apres 6 mois : make db -> make db-init -> make run"
	@echo "make init-project - tout en un : lance la BDD, attend qu'elle soit prete, cree le schema, compile"

db:
	docker run -d --name $(DB_CONTAINER) \
		-p 3306:3306 \
		-e MYSQL_ROOT_PASSWORD=root \
		-e MYSQL_DATABASE=restrictions_rentree \
		-e MYSQL_USER=admin \
		-e MYSQL_PASSWORD=admin \
		$(DB_IMAGE)
	@echo "Attends ~10s que MariaDB soit prete, puis lance 'make db-init'"

db-init:
	docker exec -i $(DB_CONTAINER) mariadb -uroot -proot -e \
		  "CREATE DATABASE IF NOT EXISTS sarapis; GRANT ALL ON sarapis.* TO 'admin'@'%'; FLUSH PRIVILEGES;"
	docker exec -i $(DB_CONTAINER) mariadb -uadmin -padmin restrictions_rentree <<-'SQL'
		  CREATE TABLE IF NOT EXISTS etablissement (
			id	    BIGINT AUTO_INCREMENT PRIMARY KEY,
			uai	   VARCHAR(255) NOT NULL,
			dateRentree DATETIME NULL DEFAULT NULL,
			enabled     TINYINT(1) DEFAULT 1
		  );
		  CREATE TABLE IF NOT EXISTS niveau (
			id		   BIGINT AUTO_INCREMENT PRIMARY KEY,
			etablissement_id BIGINT	 NOT NULL,
			nom		  VARCHAR(255) NOT NULL,
			dateRentree	DATETIME NULL,
			CONSTRAINT fk_niveau_etablissement FOREIGN KEY (etablissement_id) REFERENCES etablissement (id) ONCASCADE,
			CONSTRAINT uk_niveau UNIQUE (etablissement_id, nom)
		  );
		  CREATE TABLE IF NOT EXISTS classe (
			id	    BIGINT AUTO_INCREMENT PRIMARY KEY,
			niveau_id   BIGINT	 NOT NULL,
			nom	   VARCHAR(255) NOT NULL,
			dateRentree DATETIME NULL,
			CONSTRAINT fk_classe_niveau FOREIGN KEY (niveau_id) REFERENCES niveau (id) ON DELETE CASCADE,
			CONSTRAINT uk_classe UNIQUE (niveau_id, nom)
		  );
		  CREATE INDEX IF NOT EXISTS idx_niveau_etablissement ON niveau(etablissement_id);
		  CREATE INDEX IF NOT EXISTS idx_classe_niveau ON classe(niveau_id);
	SQL
	@echo "Schema restrictions_rentree applique, base sarapis creee (vide)."

db-down:
	docker stop $(DB_CONTAINER)

db-clean:
	docker rm -f $(DB_CONTAINER)

build:
	./mvnw clean package

run:
	./mvnw spring-boot:run

test:
	./mvnw test

clean:
	./mvnw clean
wait-db:
	@echo "Attente du demarrage de MariaDB..."
	@until docker exec $(DB_CONTAINER) mariadb-admin ping -uroot -proot --silent 2>/dev/null; do sleep 1; done
	@echo "MariaDB est prete."

init-project: db wait-db db-init build
	@echo "Projet initialise : base de donnees prete, build termine. Lance 'make run' pour demarrer l'app."