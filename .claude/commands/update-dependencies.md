---
description: Update Gradle, Kotlin, Spring Boot, plugins, libraries and the PostgreSQL image to their latest stable versions
allowed-tools: Read, Edit, Grep, Glob, WebSearch, WebFetch, mcp__context7__resolve-library-id, mcp__context7__query-docs, Bash(.\gradlew.bat:*), Bash(git diff:*), Bash(git status:*), Bash(docker info:*)
---

Update the dependencies of Catalog BE to their latest stable versions. The command has no arguments — look up all versions on the web yourself.
Do not change the application version (`version = ...` in `build.gradle.kts`); that is done by `/update-version`.

## 1. Read the current state

| What                                                                                                           | Where                                                                                                                                    |
|----------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| Gradle wrapper                                                                                                 | `gradle/wrapper/gradle-wrapper.properties` (`distributionUrl`) and `gradle-wrapper.jar`                                                  |
| Kotlin (`kotlinVersion` val), Spring Boot plugin, `io.spring.dependency-management`, `com.adarshr.test-logger` | `build.gradle.kts`, `plugins {}`                                                                                                         |
| springdoc-openapi, kotlin-logging, commons-io, openjson, mockito-kotlin (explicit versions)                    | `build.gradle.kts`, `dependencies {}`                                                                                                    |
| PostgreSQL image (`postgres:<tag>`)                                                                            | `compose.yaml` and `src/test/kotlin/com/github/vhromada/catalog/ContainerConfiguration.kt` (must be identical), mentioned in `AGENTS.md` |

Dependencies without an explicit version (Testcontainers, Mockito, JUnit, Flyway, PostgreSQL driver, Jackson, …) are managed by the Spring Boot BOM — leave them without a version.
The JDK (`eclipse-temurin:25` in `Dockerfile`, `jvmToolchain(25)` in `build.gradle.kts`) is not updated unless the user explicitly asks for it.

## 2. Look up the latest stable versions

Search the web (Gradle releases, Maven Central, Gradle Plugin Portal, Docker Hub) for the newest **stable** version of each item above.
Ignore RC, milestone, beta, alpha and SNAPSHOT versions. Use the Context7 MCP for library documentation and migration guides where useful.

- Check that the chosen Spring Boot version works with the chosen Kotlin and Gradle versions.
- Read the release notes / migration guides of every major or minor upgrade for breaking changes (also for the
  layered-jar extraction used by `Dockerfile`: `-Djarmode=tools ... extract --layers --launcher`).
- For PostgreSQL, stay on the current major unless a newer major is clearly intended; if there is a newer major, report it to the user instead of applying it.

Show a table `item | old | new` before editing anything.

## 3. Apply the updates

1. Gradle wrapper: run `.\gradlew.bat wrapper --gradle-version <version>` twice, so that `gradle-wrapper.jar` is refreshed too.
2. `build.gradle.kts`: Kotlin, Spring Boot, plugin and library versions.
3. PostgreSQL tag: `compose.yaml` and `ContainerConfiguration.kt` together, and the `postgres:<tag>` mention in `AGENTS.md`.

## 4. Verify

Docker must be running (Testcontainers) — check it with `docker info`.
Run `.\gradlew.bat clean build`. Fix breaking changes in main and test code following `AGENTS.md` (named arguments, KDoc, explicit bean names, `Result<T>` only in validators) and
re-run until the build is green.
If a fix is not obvious, stop and ask the user.

## 5. Report

Do not commit or push. Report:
- the table of updated versions,
- code changes made because of breaking changes,
- the result of the build and tests,
- the suggested commit message in the style of the git history: `gradle: <version>, kotlin: <version>, spring boot: <version>` (add `, java: <version>` only when the JDK changed).
