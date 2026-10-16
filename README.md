# E-commerce API

API REST e-commerce développée avec **Spring Boot 3 / Java 21**, conteneurisée avec **Docker** et déployée sur **Render**. Projet d'apprentissage : il centralise catégories, produits, utilisateurs, commandes et images, et peut être consommé par plusieurs clients (par exemple une application Angular).

## 🌐 Démo

| | Lien |
|---|---|
| Swagger UI | https://ecommerce-api-1eu4.onrender.com/swagger-ui.html |
| Health | https://ecommerce-api-1eu4.onrender.com/actuator/health |
| Image Docker | https://hub.docker.com/r/moinammaoueni/e-commerce-api |

> ⏱️ Hébergée sur une offre gratuite : après une période d'inactivité, le premier appel peut prendre ~50 secondes.

## 🧰 Technologies

- Java 21, Spring Boot 3.5
- Spring Web, Spring Data JPA / Hibernate
- Spring Security + JWT
- PostgreSQL 16
- Springdoc OpenAPI / Swagger UI
- Spring Boot Actuator (healthchecks)
- Maven, Docker, Docker Compose

## ✨ Fonctionnalités

- **Authentification** : inscription, connexion, JWT, rôles (utilisateur / admin)
- **Catégories** : consultation publique, CRUD côté administration
- **Produits** : pagination, recherche par nom, filtre par catégorie, gestion du stock et des prix
- **Images** : téléversement, consultation, suppression
- **Commandes** : création, historique de l'utilisateur, gestion côté administration

## 🏗️ Architecture

```text
Clients (Angular, autres)
        │  HTTP / JSON / JWT
        ▼
  Spring Boot API ──► PostgreSQL
        │
        └──► Stockage des images (volume Docker)
```

Séparation en couches : contrôleurs, services, repositories, entités, DTO.

## 🐳 Docker

| Élément | Détail |
|---|---|
| `Dockerfile` | Multi-stage : build Maven (JDK 21) puis image finale JRE 21 Alpine |
| Cache | `pom.xml` copié avant `src` pour réutiliser les dépendances |
| Sécurité | Conteneur exécuté avec un utilisateur non-root |
| Mémoire | `mem_limit` + `-XX:MaxRAMPercentage=75` adaptés à la JVM |
| Healthchecks | Base : `pg_isready` · API : `/actuator/health` |
| Données | Volumes nommés : base PostgreSQL et images uploadées |
| Fichiers Compose | `docker-compose.yml` (dev) · `docker-compose.prod.yml` (prod) |
| Profils Spring | `dev` (logs SQL) · `prod` (logs sobres) |

## 🚀 Lancer le projet avec Docker (recommandé)

**Prérequis** : Docker Desktop, Git.

```bash
git clone https://github.com/Moina337/e-commerce.git
cd e-commerce/e-commerce.api
copy .env.example .env      # Linux/Mac : cp .env.example .env
```

Renseigne le `.env` (voir la table ci-dessous), puis :

```bash
docker compose up -d --build
docker compose ps
```

Quand l'API est `healthy` : http://localhost:8080/swagger-ui.html

Arrêt : `docker compose down` (les données sont conservées).
⚠️ `docker compose down -v` **supprime les volumes**, donc la base.

### Mode production (local)

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

Port de la base non publié, `restart: unless-stopped`, profil `prod`.

## ⚙️ Variables d'environnement

| Variable | Exemple | Rôle |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` / `prod` | Profil Spring |
| `SERVER_PORT` | `8080` | Port de l'API |
| `POSTGRES_DB` | `ecommerce_db` | Nom de la base |
| `POSTGRES_USERNAME` | `postgres` | Utilisateur |
| `POSTGRES_PASSWORD` | *(secret)* | Mot de passe |
| `POSTGRES_URL` | `jdbc:postgresql://db:5432/ecommerce_db` | URL **JDBC** |
| `JWT_SECRET` | *(secret long et aléatoire)* | Signature des tokens |
| `JWT_EXPIRATION` | `86400000` | Durée du token (ms) |
| `APP_STORAGE_LOCATION` | `/app/uploads/images` | Dossier des images |
| `APP_STORAGE_MAX_FILE_SIZE` | `5MB` | Taille max d'un fichier |
| `APP_STORAGE_MAX_REQUEST_SIZE` | `10MB` | Taille max d'une requête |

> Le `.env` n'est **jamais** versionné. Dans Compose, l'URL de la base utilise le nom du service (`db`), pas `localhost`.

## 💻 Lancer sans Docker

Prérequis : Java 21, Maven, PostgreSQL.

1. Crée la base `ecommerce_db`.
2. Définis les variables ci-dessus dans ton IDE ou ton terminal (Spring Boot ne lit pas un `.env` automatiquement).
3. `./mvnw spring-boot:run` (ou `mvn spring-boot:run`).

## ☁️ Déploiement sur Render

L'image est construite et publiée sur Docker Hub, puis Render la tire :

```bash
docker build -t moinammaoueni/e-commerce-api:1.0 .
docker push moinammaoueni/e-commerce-api:1.0
```

Sur Render : service de type **Image**, avec une base PostgreSQL managée. Variables d'environnement à définir dans le service :

- `POSTGRES_URL` au format **JDBC** : `jdbc:postgresql://HÔTE:5432/BASE?sslmode=require` (l'URL `postgresql://` fournie par Render n'est pas valide pour Java)
- `POSTGRES_USERNAME` et `POSTGRES_PASSWORD` séparés de l'URL
- `JWT_SECRET` long et propre à la production
- `SPRING_PROFILES_ACTIVE=prod`

⚠️ Le système de fichiers d'un service Render est éphémère : les images uploadées nécessitent un disque persistant ou un stockage externe.

## 💾 Sauvegarde et restauration

```bash
# Sauvegarde
docker compose exec -T db pg_dump -U postgres -d ecommerce_db > sauvegarde.sql

# Restauration (base de test)
docker compose exec db psql -U postgres -c "CREATE DATABASE restore_test;"
docker compose exec -T db psql -U postgres -d restore_test < sauvegarde.sql
```

Un volume Docker n'est pas une sauvegarde : planifier des exports et les copier hors du serveur.

## 🔐 Sécurité et limites connues

- Secrets fournis par variables d'environnement
- Endpoints protégés par JWT, règles d'accès côté backend
- `/actuator/health` est le seul endpoint Actuator exposé
- `ddl-auto: update` est utilisé : en production réelle, préférer `validate` + migrations (Flyway/Liquibase)
- CORS à restreindre aux origines nécessaires

## 🗺️ Suite prévue

Frontend Angular · CI/CD avec GitHub Actions · migrations Flyway

## 📄 Licence

Projet personnel d'apprentissage.
