# Catalog BE

Spring Boot / Kotlin REST API for a personal catalog of movies, shows, games, music, programs, books and jokes.

Besides those, the API also manages supporting resources (authors, genres, cheats, pictures) and account/role/register data.

## Tech stack

- Kotlin, Spring Boot, JDK 25
- Gradle (wrapper)
- PostgreSQL with Flyway migrations
- Tests: JUnit 5, Mockito, AssertJ, Testcontainers

## Prerequisites

- JDK 25
- Docker (local PostgreSQL via `compose.yaml` and Testcontainers-based integration tests)

## Getting started

Start the local database (`localhost:5432/catalog`, user/password `catalog`/`catalog`) and run the application:

```powershell
docker compose up -d postgres
.\gradlew.bat bootRun
```

To run the whole stack (database and application, exposed on port 8100) in Docker:

```powershell
docker compose up -d
```

## Build and test

```powershell
.\gradlew.bat build
.\gradlew.bat test
.\gradlew.bat test --tests com.github.vhromada.catalog.common.TimeTest
```

## API

- Resources are available under `rest/<resource>` (e.g. `rest/genres`).
- Swagger UI: `/swagger-ui.html`.
- Authentication: HTTP Basic, default user `catalog` / `catalog` (`catalog.security.username`, `catalog.security.password`).
- Unauthenticated: `/rest/public/**` and the Actuator endpoints under `/app` (not `/actuator`).
- Optional request headers `X-Request-Id` and `X-User` are echoed back in the response.

## Configuration

Defaults are in `src/main/resources/application.yml` and can be overridden with environment variables:

| Variable                     | Description                                                   |
|------------------------------|---------------------------------------------------------------|
| `CATALOG_SECURITY_USERNAME`  | API user name                                                 |
| `CATALOG_SECURITY_PASSWORD`  | API user password                                             |
| `SPRING_DATASOURCE_URL`      | JDBC URL (default `jdbc:postgresql://localhost:5432/catalog`) |
| `SPRING_DATASOURCE_USERNAME` | Database user                                                 |
| `SPRING_DATASOURCE_PASSWORD` | Database password                                             |

## Project structure

Code lives in `src/main/kotlin/com/github/vhromada/catalog` and is layered as `Controller → Facade → Service → Repository`. API DTOs are in `entity`, JPA entities in `domain`, and `mapper`
and `validator` packages connect and guard them. The schema is a single Flyway migration, `src/main/resources/db/migration/V1.0__Catalog.sql`.

## License

[MIT](LICENSE)
