# Project Guidelines

## Build/Configuration Instructions

### Prerequisites
- **JDK 25**: The project is configured to use JVM 25.
- **Docker**: Required for running integration tests via Testcontainers.

### Build Commands
- **Build project**: `.\gradlew.bat build`
- **Clean project**: `.\gradlew.bat clean`
- **Run application**: `.\gradlew.bat bootRun`

The build process uses the Gradle Wrapper (`gradlew` or `gradlew.bat`), ensuring the correct Gradle version is used.

---

## Testing Information

### Configuration
Tests are configured in `build.gradle.kts`. Key configurations include:
- **Testcontainers**: Used for PostgreSQL integration tests (image `postgres:18.3`).
- **Flyway**: Automatically handles database migrations before tests.
- **Mockito**: Used for mocking in unit tests.
- **AssertJ**: Preferred assertion library.

### Running Tests
- **All tests**: `.\gradlew.bat test`
- **Specific test class**: `.\gradlew.bat test --tests com.github.vhromada.catalog.SimpleTest`
- **Specific test method**: `.\gradlew.bat test --tests "com.github.vhromada.catalog.SimpleTest.simple math test"`

### Adding New Tests
- **Unit Tests**: Use `@ExtendWith(MockitoExtension::class)` and mock dependencies. Place them in `src/test/kotlin` under the corresponding package.
- **Integration Tests**: Use `@SpringBootTest`, `@Testcontainers`, and `@ContextConfiguration(classes = [TestConfiguration::class])` for tests requiring a full Spring context and database.

### Simple Test Example
```kotlin
package com.github.vhromada.catalog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SimpleTest {

    @Test
    fun `simple math test`() {
        assertThat(1 + 1).isEqualTo(2)
    }
}
```

---

## Additional Development Information

### Result Pattern
The project uses a custom `Result<T>` class for handling operation outcomes, especially in facades and services. It encapsulates:
- `data`: The actual payload (if any).
- `status`: `OK`, `WARN`, or `ERROR`.
- `events`: A list of `Event` objects containing `severity`, `key`, and `message`.

**Example Usage**:
```kotlin
fun process(input: String): Result<String> {
    if (input.isEmpty()) {
        return Result.error("EMPTY_INPUT", "Input cannot be empty.")
    }
    return Result.of("Processed: $input")
}
```

### Code Style
- Follow standard Kotlin coding conventions.
- Use KDoc for classes and public methods.
- Prefer `val` over `var` where possible.
- Use expressive test names (backticks are allowed for descriptive names).

### Logging
- Use `io.github.oshai:kotlin-logging` for logging.
- Example: `private val logger = KotlinLogging.logger {}`

### Database Migrations
- Flyway migrations are located in `src/main/resources/db/migration`.
- Follow the naming convention `V<version>__<description>.sql`.
