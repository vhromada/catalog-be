# Catalog BE shared conventions

Referenced by every layer skill in `.claude/skills/`. Each layer skill also carries a short inline summary of the parts that matter most for that layer, so it works standalone — read this file when
you need the full picture or a cross-layer rule.

## Canonical reference resource: Genre

Genre is the simplest resource in the codebase (a single `name` field, no parent/child relationship) and is the template source for every layer skill. When in doubt, re-read the real file — don't rely
on a skill's snippet from memory, since the snippet may lag the code.

| Layer             | File(s)                                                                                                                                   |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| Migration         | New `V<next-major>.0__<Resource>.sql` (reference example: genres table in `V1.0__Catalog.sql`, lines ~24-36; never edit V1.0)             |
| Test data         | `src/test/resources/db/migration/V99999999.0__TestData.sql` (genres inserts)                                                              |
| Domain            | `src/main/kotlin/com/github/vhromada/catalog/domain/Genre.kt`                                                                             |
| Domain filter     | `src/main/kotlin/com/github/vhromada/catalog/domain/filter/GenreFilter.kt`                                                                |
| Repository        | `src/main/kotlin/com/github/vhromada/catalog/repository/GenreRepository.kt`                                                               |
| Service           | `src/main/kotlin/com/github/vhromada/catalog/service/GenreService.kt` + `service/impl/GenreServiceImpl.kt`                                |
| Facade            | `src/main/kotlin/com/github/vhromada/catalog/facade/GenreFacade.kt` + `facade/impl/GenreFacadeImpl.kt`                                    |
| Mapper            | `src/main/kotlin/com/github/vhromada/catalog/mapper/GenreMapper.kt` + `mapper/impl/GenreMapperImpl.kt`                                    |
| Validator         | `src/main/kotlin/com/github/vhromada/catalog/validator/GenreValidator.kt` + `validator/impl/GenreValidatorImpl.kt`                        |
| Controller        | `src/main/kotlin/com/github/vhromada/catalog/controller/GenreController.kt`                                                               |
| Entity DTO        | `src/main/kotlin/com/github/vhromada/catalog/entity/Genre.kt`                                                                             |
| Entity request    | `src/main/kotlin/com/github/vhromada/catalog/entity/io/ChangeGenreRequest.kt`                                                             |
| Entity statistics | `src/main/kotlin/com/github/vhromada/catalog/entity/GenreStatistics.kt`                                                                   |
| Unit tests        | `src/test/kotlin/com/github/vhromada/catalog/service/GenreServiceTest.kt`, `mapper/GenreMapperTest.kt`, `validator/GenreValidatorTest.kt` |
| Integration tests | `src/test/kotlin/com/github/vhromada/catalog/facade/GenreFacadeSpringTest.kt`, `repository/GenreRepositorySpringTest.kt`                  |
| Test data builder | `src/test/kotlin/com/github/vhromada/catalog/utils/GenreUtils.kt`                                                                         |

## KDoc phrasing

Every public class, constructor property, and method gets a KDoc comment — match the density of the existing code (it documents every constructor property too, not just the class).

- Interface class doc: `A class represents <X> for <resources>.` for domain/entity data classes, or `An interface represents <X> for <resources>.` for interfaces
  (service/facade/mapper/validator/repository).
- Impl class doc: `A class represents implementation of <X> for <resources>.`
- Every class ends its KDoc with a blank line then `@author Vladimir Hromada`.
- Method docs use `@param`, `@return`, `@throws InputException if ...` as applicable (see each layer skill for exact phrasing of its methods).

## Bean naming

Every `@RestController`/`@Service`/`@Component`-annotated class gets an explicit bean name — the decapitalized interface/class name:

```kotlin
@Service("genreService")
@Component("genreFacade")
@Component("genreMapper")
@Component("genreValidator")
@RestController("genreController")
```

Never a bare `@RestController`/`@Service`/`@Component`. Spring Data JPA repositories (`repository/`) are plain interfaces with **no** stereotype annotation — this rule doesn't apply there.

## Constructor injection & named arguments

- Constructor injection only, never field/setter injection.
- Always use named arguments when calling a project-defined function, method, or constructor — including single-argument calls — in both main and test source (e.g.
  `Event(severity = Severity.ERROR, key = "...", message = "...")`, `InputException(result = result)`, `facade.update(uuid = uuid, request = request)`).
- This does **not** apply to Kotlin stdlib/JDK/Spring calls (`repository.save(author)`, `Sort.by(...)`) — leave those positional.

## Package layout

Each layer with business logic follows an interface + `impl` package split, named `<Name><Layer>[Impl]`: `com.github.vhromada.catalog.<layer>` holds the interface,
`com.github.vhromada.catalog.<layer>.impl` holds the implementation. Domain, entity, and repository have no interface/impl split.

```
Controller → Facade → Service → Repository
```

## Verification commands

- `.\gradlew.bat build` — full build including tests; Docker must be running (Testcontainers).
- `.\gradlew.bat test --tests <fqn>` — a single test class or method, e.g. `.\gradlew.bat test --tests com.github.vhromada.catalog.service.GenreServiceTest`.
- Never use a global `gradle`/`gradlew` — always `.\gradlew.bat` on this Windows project.

## Composite resources (have more than one name field, e.g. Author, Movie, Show)

Reuse `ValidationSupport.validateNames(czechName, originalName, prefix, result)` (czech/original name pairs) and
`ValidationSupport.validateItems(items, collectionKey, itemKey, collectionLabel, itemLabel, result)` (list-of-strings fields such as subtitles/genres) from `validator/utils/ValidationSupport.kt`
instead of hand-rolling the same `when` branches. See `validator/impl/ShowValidatorImpl.kt`, `MovieValidatorImpl.kt`, `BookValidatorImpl.kt` for usage. Likewise reuse
`entity/filter/MultipleNameFilter.kt` (czech + original name) when a composite resource's search filter fits that shape instead of a new filter class.
