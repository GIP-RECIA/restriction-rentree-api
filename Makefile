.PHONY: help db db-init db-down db-clean wait-db init-project run build test clean
SHELL := /bin/bash
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
	@if [ "$$(docker ps -aq -f name=^$(DB_CONTAINER)$$)" ]; then \
		  echo "Conteneur $(DB_CONTAINER) existe deja, demarrage..."; \
		  docker start $(DB_CONTAINER); \
	else \
		  docker run -d --name $(DB_CONTAINER) \
			    -p 3306:3306 \
			    -e MYSQL_ROOT_PASSWORD=root \
			    -e MYSQL_DATABASE=restrictions_rentree \
			    -e MYSQL_USER=admin \
			    -e MYSQL_PASSWORD=admin \
			    $(DB_IMAGE); \
	fi
	@echo "Attends ~10s que MariaDB soit prete, puis lance 'make db-init'"

db-init:
	docker exec -i $(DB_CONTAINER) mariadb -uroot -proot -e \
		  "CREATE DATABASE IF NOT EXISTS sarapis; GRANT ALL ON sarapis.* TO 'admin'@'%'; FLUSH PRIVILEGES;"
	docker exec -i $(DB_CONTAINER) mariadb -uadmin -padmin restrictions_rentree < sql/restrictions_rentree.sql
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
	@until docker exec $(DB_CONTAINER) mariadb -uroot -proot -e "SELECT 1" > /dev/null 2>&1; do sleep 1; done
	@echo "MariaDB est prete."

init-project: db wait-db db-init build
	@echo "Projet initialise : base de donnees prete, build termine. Lance 'make run' pour demarrer l'app."