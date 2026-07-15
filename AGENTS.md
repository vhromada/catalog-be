# AI Agent Guidelines – Catalog BE

This document provides essential information for AI agents (Claude, Copilot, Cursor, etc.) working on the Catalog BE project.

## 🛠 Tech Stack
- **Language:** Kotlin
- **Framework:** Spring Boot
- **JVM:** JDK 25
- **Build Tool:** Gradle (using `gradlew.bat`)
- **Database:** PostgreSQL (Migrations handled by Flyway)
- **Testing:** JUnit 5, Mockito, AssertJ, Testcontainers
- **Prerequisites:** JDK 25, Docker (required both for Testcontainers-based integration tests and for running a local PostgreSQL via `compose.yaml`)

## 🚀 Key Commands (Windows PowerShell)
- **Build:** `.\gradlew.bat build`
- **Clean:** `.\gradlew.bat clean`
- **Start local DB:** `docker compose up -d postgres` (starts PostgreSQL from `compose.yaml` on `localhost:5432/catalog`, user/password `catalog`/`catalog`) — required before `bootRun`, since `application.yml` expects the DB already running.
- **Run App:** `.\gradlew.bat bootRun`
- **Run All Tests:** `.\gradlew.bat test`
- **Run Specific Test:** `.\gradlew.bat test --tests <fqn>` (e.g. `.\gradlew.bat test --tests com.github.vhromada.catalog.common.TimeTest` or `.\gradlew.bat test --tests "com.github.vhromada.catalog.common.TimeTest.length"`)

## 🧩 Architectural Patterns

### Result Pattern
Facades and services return the actual entity/DTO (or throw) — they do **not** return `Result<T>`. `Result<T>` is used internally by validators to accumulate validation failures, which are then thrown as an `InputException` and turned into an HTTP response by `RestExceptionHandler`.
- **Payload:** `data`
- **Status:** `OK`, `WARN`, `ERROR`
- **Events:** List of `Event` (severity, key, message)

**Usage Example (validator, verbatim from `validator/impl/GenreValidatorImpl.kt`):**
```kotlin
override fun validateRequest(request: ChangeGenreRequest) {
    val result = Result<Unit>()
    when {
        request.name == null -> {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GENRE_NAME_NULL", message = "Name mustn't be null."))
        }

        request.name.isEmpty() -> {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GENRE_NAME_EMPTY", message = "Name mustn't be empty string."))
        }
    }
    if (result.isError()) {
        throw InputException(result = result)
    }
}
```

## 🔐 Auth
Two independent layers:
- **Request auth context:** header-based, not JWT/OAuth. `X-Request-Id` and `X-User` headers (`common/auth/Header.kt`) are read by `AuthContextFilter` into a request-scoped `AuthContextHolder`/`AuthContext` (via `AuthContextFactory`) and echoed back on the response; the filter also catches `InputException` and writes the JSON error body directly, bypassing `RestExceptionHandler`.
- **Spring Security:** configured in `CatalogSecurityConfiguration.kt` — HTTP Basic auth, a single in-memory user (`catalog`/`catalog`, role `USER`), CORS enabled, CSRF disabled, `/rest/public/**` and `/app`, `/app/*` open, everything else requires authentication. No method-level RBAC (`@PreAuthorize`/`@Secured`) anywhere.

## 🧪 Testing Strategy
- **Unit Tests:** Use `@ExtendWith(MockitoExtension::class)`. Place in `src/test/kotlin`, mirroring the `src/main/kotlin` package layout.
- **Integration Tests (facade/service/repository):** Named `*SpringTest`. Use `@ExtendWith(SpringExtension::class)`, `@ContextConfiguration(classes = [TestConfiguration::class])`, `@Transactional`, `@Rollback`. `TestConfiguration.kt` imports `ContainerConfiguration.kt` (a `@ServiceConnection`-annotated `postgres:18.4` Testcontainer; Flyway migrations run against it automatically) plus `CatalogConfiguration`, overriding `AuditorAware`, `TimeProvider`, `PasswordEncoder` (no-op), `NormalizerService`, and CORS beans for deterministic tests. `@SpringBootTest` itself is only used by `CatalogContextTest`, a plain context-loads smoke test — not the general integration pattern.
- **Controller Tests:** Use `@WebMvcTest(<Controller>::class)` + `@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)`, mocking the facade with `@MockitoBean` (e.g. `AuthorControllerTest`).
- **Test Data:** Reuse the per-entity test data builders in `src/test/kotlin/com/github/vhromada/catalog/utils` (e.g. `GenreUtils`, `MovieUtils`) instead of hand-building domain/entity objects in new tests.
- **Assertions:** Prefer AssertJ.
- **Naming:** Use expressive names, backticks allowed (e.g., ` `should return error when input is empty` `).

## 📝 Coding Standards
- **Style:** Standard Kotlin conventions.
- **Immutability:** Prefer `val` over `var`.
- **Documentation:** Use KDoc for all public classes and methods (existing code documents every constructor property too — match that density in new files).
- **Logging:** Use `io.github.oshai:kotlin-logging`.
  - `private val logger = KotlinLogging.logger {}`
- **Database Migrations:** Located in `src/main/resources/db/migration`.
  - Format: `V<version>__<description>.sql`

## 📂 Project Structure
Each layer with business logic follows an interface + `impl` package split, named `<Name><Layer>[Impl]` (e.g. `facade/GenreFacade.kt` + `facade/impl/GenreFacadeImpl.kt`):

```
Controller → Facade → Service → Repository
```

- `src/main/kotlin/com/github/vhromada/catalog`:
  - Root package also holds the Spring bootstrap/config classes directly (no `config` subpackage): `CatalogApplication.kt` (entrypoint), `CatalogConfiguration.kt`, `CatalogSecurityConfiguration.kt` (see Auth below), `CatalogWebConfiguration.kt`.
  - `common`: `common/auth` — request-scoped auth context (`AuthContext`, `AuthContextHolder`, `AuthContextFilter`) populated from request headers; `common/log` — logging helpers; `common/result` — the `Result<T>`/`Event` types (see Result Pattern above).
  - `utils`: shared constants (`Constants.kt`) — distinct from the test-only `src/test/kotlin/.../utils` package of test data builders mentioned below.
  - `controller`: `@RestController`s, one flat package, each mapped to a `rest/<resource>` URL path (e.g. `GenreController` → `@RequestMapping("rest/genres")`). Thin: delegate straight to a facade, no logic.
  - `facade`: orchestrates a use case — runs the relevant `validator`, calls the `service`, and uses a `mapper` to convert between `entity` and `domain`.
  - `service`: domain logic, talks to `repository`. Operates on `domain` objects.
  - `repository`: Spring Data JPA repositories over `domain` entities.
  - `entity`: API-facing DTOs (request/response shapes), including `entity/filter`, `entity/io`.
  - `domain`: JPA-mapped persistence entities (distinct from `entity`), including `domain/filter`, `domain/io`.
  - `mapper`: converts between `entity` DTOs and `domain` entities (and vice versa).
  - `validator`: Business validation.
  - `exception`: `InputException` (validation failure) and `RestExceptionHandler`, a `@ControllerAdvice` mapping it to an HTTP response via `IssueMapper`.
  - `provider`: supporting integrations (non-CRUD collaborators).
- `compose.yaml`: local dev setup — `postgres` service (used by `bootRun`) and `catalog` service (builds/runs the app via `Dockerfile`).
- `Dockerfile`: builds the deployable app image (used by the `catalog` service in `compose.yaml`).

## 🤖 Agent Instructions
When working on this project, always:
1. **Build & Test:** Use `.\gradlew.bat` for all tasks. Never use global `gradle` or `gradlew` (unless on Unix).
2. **Architecture:** Use `Result<T>`/`Event` inside validators and throw `InputException` on failure; facade and service methods return the entity/DTO directly, not `Result<T>`.
3. **Tests:** 
   - Ensure new features have both unit and integration tests.
   - Use Testcontainers for integration tests.
   - Use expressive names with backticks for test methods.
4. **Database:** Add Flyway migrations for any schema changes in `src/main/resources/db/migration`.
5. **Documentation:** Write KDoc for all new public classes and methods.
6. **Code Style:** Use `val` for immutability and follow Kotlin naming conventions.
