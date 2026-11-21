---
name: layer-tests
description: >-
  Use when writing or changing tests in catalog-be — unit tests for service/mapper, validator tests, @WebMvcTest controller tests, *SpringTest integration tests for facade/repository, or a test-data
  builder under utils/. Covers MockitoExtension conventions, verifyNoMoreInteractions discipline, Testcontainers/SequenceResetTestExecutionListener, and keeping XUtils in sync with the Flyway
  test-data file.
---

# Layer tests

## Scope

Everything under `src/test/kotlin/com/github/vhromada/catalog/`, mirroring `src/main/kotlin`'s package layout: unit tests (service/mapper), validator tests, `@WebMvcTest` controller tests,
`*SpringTest` integration tests (facade + repository), and the per-resource test-data builder in `utils/`.

Inline convention summary (full detail below): every mock-based test ends with `verifyNoMoreInteractions(...)`/`verifyNoInteractions(...)` on every collaborator; `*SpringTest` classes share one cached
Spring context/Testcontainer and rely on `SequenceResetTestExecutionListener` instead of `@DirtiesContext`; only facade and repository get `*SpringTest`s — service/mapper/validator are Mockito unit
tests, and the test-data builder's fixtures must mirror `V99999999.0__TestData.sql` exactly.

## Files (Genre reference)

| File                                                                  | Purpose                                                                                  |
|-----------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| `service/GenreServiceTest.kt`                                         | unit test, MockitoExtension                                                              |
| `mapper/GenreMapperTest.kt`                                           | unit test, MockitoExtension                                                              |
| `validator/GenreValidatorTest.kt`                                     | plain test, no extension                                                                 |
| `controller/AuthorControllerTest.kt`                                  | `@WebMvcTest` reference (Genre has no controller test on its own; Author is the pattern) |
| `facade/GenreFacadeSpringTest.kt`                                     | integration test                                                                         |
| `repository/GenreRepositorySpringTest.kt`                             | integration test                                                                         |
| `utils/GenreUtils.kt`                                                 | test-data builder                                                                        |
| `TestConfiguration.kt` / `ContainerConfiguration.kt`                  | shared Spring test context / Testcontainers setup                                        |
| `utils/AuditUtils.kt`, `utils/TestConstants.kt`, `utils/PageUtils.kt` | shared test helpers                                                                      |
| `META-INF/spring.factories`                                           | registers `SequenceResetTestExecutionListener`                                           |

## Unit tests (service, mapper)

`@ExtendWith(MockitoExtension::class)`, mocks via `org.mockito.kotlin` (`any()`, `eq()`, `whenever()`, `verify()`, `argumentCaptor<T>()`), SUT constructed in `@BeforeEach` with named arguments,
camelCase test method names (`getExisting`, `addNullName` — **not** backticked descriptions despite what AGENTS.md's general Testing Strategy section says; that's the real, observed convention in
every `*ServiceTest`/`*MapperTest`). Every test ends with `verifyNoMoreInteractions(...)` on every mock it used, plus `verifyNoInteractions(...)` on every mock it didn't touch.

```kotlin
@ExtendWith(MockitoExtension::class)
class GenreServiceTest {

    @Mock
    private lateinit var repository: GenreRepository

    @Mock
    private lateinit var uuidProvider: UuidProvider

    private lateinit var service: GenreService

    @BeforeEach
    fun setUp() {
        service = GenreServiceImpl(repository = repository, uuidProvider = uuidProvider)
    }

    @Test
    fun getExisting() {
        val genre = GenreUtils.getDomainGenre(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(genre))

        val result = service.get(uuid = genre.uuid)

        assertThat(result).isEqualTo(genre)
        verify(repository).findByUuid(uuid = genre.uuid)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    // ...

}
```

- One test method per behavior (happy path + each distinct branch, e.g. `search` / `searchEmptyFilter`, `getExisting` / `getNotExisting`).
- Assertions via AssertJ (`assertThat`, `assertThatThrownBy`); a thrown `InputException` is asserted with
  `.isInstanceOf(InputException::class.java).hasMessageContaining("<KEY>").hasMessageContaining("<message>").hasFieldOrPropertyWithValue("httpStatus", HttpStatus.X)`.
- A domain type reused as a Mockito argument matcher needs a tiny private helper when `any()` alone is ambiguous, e.g. `private fun anyDomain(): Genre = any()` — follow this pattern rather than
  casting at the call site.
- Mapper tests follow the identical shape, mocking `NormalizerService`/`UuidProvider` instead of a repository.

## Validator tests

No `@ExtendWith` — validators have no collaborators to mock. SUT built in `@BeforeEach`, `assertThatThrownBy` on each failing case, asserting key + message + `HttpStatus.UNPROCESSABLE_CONTENT` (422):

```kotlin
class GenreValidatorTest {

    private lateinit var validator: GenreValidator

    @BeforeEach
    fun setUp() {
        validator = GenreValidatorImpl()
    }

    @Test
    fun validateRequest() {
        validator.validateRequest(request = GenreUtils.newRequest())
    }

    @Test
    fun validateRequestNullName() {
        val request = GenreUtils.newRequest().copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)
    }

}
```

One passing-case test plus one test per failing field/branch, named `validateRequest<Condition>` (`validateRequestNullName`, `validateRequestEmptyName`).

## Controller tests

`@WebMvcTest(<X>Controller::class)` + `@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)`; mock the facade **and** `IssueMapper` with `@MockitoBean`; use
`tools.jackson.databind.ObjectMapper` (Jackson 3, autowired) to serialize requests/expected responses; add `.with(csrf())` on every POST/PUT/DELETE (CSRF is disabled in prod config, but
`@WebMvcTest`'s default Spring Security test setup still enforces it unless overridden — match the existing tests rather than disabling it); every test ends `verifyNoMoreInteractions(facade)` +
`verifyNoInteractions(issueMapper)`.

```kotlin
@WebMvcTest(GenreController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class GenreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var facade: GenreFacade

    @MockitoBean
    private lateinit var issueMapper: IssueMapper

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun get() {
        val genre = GenreUtils.getGenre(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(genre)

        mockMvc.perform(get("/rest/genres/${genre.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(genre)))

        verify(facade).get(uuid = genre.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    // search / searchFilter use argumentCaptor<...Filter>() + assert each field (see AuthorControllerTest for the full pattern
    // including query-param binding for a multi-field filter), add / update use .with(csrf()).content(objectMapper.writeValueAsString(request)),
    // remove expects status().isNoContent(), duplicate/add expect status().isCreated().

}
```

Write one test per controller method (`search`, `searchFilter` when the filter has more than a trivial shape, `get`, `add`, `update`, `remove`, `duplicate`, `getStatistics`), matching
`AuthorControllerTest`'s structure exactly for request building and assertions.

## Integration tests (`*SpringTest`)

Only the **facade** and **repository** layers get `*SpringTest`s — not service, not controller, not mapper, not validator.
`@ExtendWith(SpringExtension::class) @ContextConfiguration(classes = [TestConfiguration::class]) @Transactional @Rollback`. Inject `EntityManager` via `@PersistenceContext` for direct DB
assertions/flushes; inject the real facade/repository bean via `@Autowired`.

```kotlin
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class GenreFacadeSpringTest {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var facade: GenreFacade

    @Test
    fun add() {
        val expectedGenre = GenreUtils.newGenre()
        val expectedDomainGenre = GenreUtils.newDomainGenre(id = GenreUtils.GENRES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = GenreUtils.newRequest())
        entityManager.flush()

        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = result, ignoreUuid = true)
        GenreUtils.assertGenreDeepEquals(expected = expectedDomainGenre, actual = GenreUtils.getDomainGenre(entityManager = entityManager, id = GenreUtils.GENRES_COUNT + 1), ignoreUuid = true)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT + 1)
    }

}
```

- `TestConfiguration` (`src/test/kotlin/com/github/vhromada/catalog/TestConfiguration.kt`) imports `CatalogConfiguration` + `ContainerConfiguration` (a `@ServiceConnection`-annotated `postgres:18.7`
  Testcontainer that Flyway migrates automatically) and overrides `AuditorAware` (fixed to account index 2), `TimeProvider` (fixed to `TestConstants.TIME`), `PasswordEncoder` (no-op),
  `NormalizerService` (identity function), and CORS — for fully deterministic assertions. Don't re-override these per-test.
- After a mutating call, `entityManager.flush()` before asserting on data fetched through the `EntityManager`, so the write is visible.
- Assert both the DTO/domain result **and** the row count (`getXCount(entityManager = entityManager)`) in every test, including the negative/error-path tests (count must stay unchanged after a failed
  `add`/`update`).
- A resource that's referenced by other tables (Genre by `movie_genres`/`tv_show_genres`) needs a `clearReferencedData()` private helper before `remove`/`removeAll`, deleting the join rows first via a
  `@Suppress("SqlNoDataSourceInspection", "SqlWithoutWhere", "SqlResolve")` native query — copy this verbatim, adjusting table names, when your resource has FK references from elsewhere.
- Repository tests additionally cover `searchByFilter` (via `filter.toSpecification()`) and `findByUuid`, beyond the CRUD methods the facade test already exercises.

### Testcontainers / sequence reset — don't fight these

- `ContainerConfiguration` is shared and cached across **all** `*SpringTest` classes (one Spring context, one PostgreSQL container). `CatalogContextTest` (`@SpringBootTest`) is the one exception — it
  builds its own separate context/container, and is a plain smoke test, not a pattern to copy.
- `@Rollback` undoes row changes but **not** sequence advances. Tests expecting a specific new id (`GenreUtils.GENRES_COUNT + 1`) rely on `SequenceResetTestExecutionListener` (registered in
  `src/test/resources/META-INF/spring.factories`), which snapshots `pg_sequences` before each test method and restores them after. **Never reach for `@DirtiesContext`** to solve a sequence/id
  flakiness problem — it tears down the shared cached context for every other test class and is explicitly the wrong tool here.
- **Caveat inherited from AGENTS.md:** because a whole `*SpringTest` method runs inside one shared transaction/session, these tests cannot catch a bug where an entity crosses a *closed* session
  boundary (e.g. the facade fetching a child in one call, passing it to another `@Transactional` service method) — see the pitfall in `service-layer`. A green `*SpringTest` run is not proof that
  cross-call code is safe; only a separate-process E2E suite outside this repo can prove that.

## Test-data builder (`utils/<X>Utils.kt`)

```kotlin
object GenreUtils {

    const val GENRES_COUNT = 4

    fun getDomainGenres(): List<com.github.vhromada.catalog.domain.Genre> { /* indices 1..GENRES_COUNT */ }

    fun getGenres(): List<Genre> { /* indices 1..GENRES_COUNT */ }

    fun getDomainGenre(index: Int): com.github.vhromada.catalog.domain.Genre {
        val name = "Genre $index name"
        return com.github.vhromada.catalog.domain.Genre(id = index, uuid = getUuid(index = index), name = name, normalizedName = name)
            .fillAudit(audit = AuditUtils.getAudit())
    }

    private fun getUuid(index: Int): String { /* fixed, deterministic UUID literal per index — must match V99999999.0__TestData.sql */ }

    fun getDomainGenre(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Genre? =
        entityManager.find(com.github.vhromada.catalog.domain.Genre::class.java, id)

    fun getGenre(index: Int): Genre = Genre(uuid = getUuid(index = index), name = "Genre $index name")

    fun getGenresCount(entityManager: EntityManager): Int =
        entityManager.createQuery("SELECT COUNT(g.id) FROM Genre g", Long::class.java).singleResult.toInt()

    fun newDomainGenre(id: Int?): com.github.vhromada.catalog.domain.Genre =
        com.github.vhromada.catalog.domain.Genre(id = id, uuid = TestConstants.UUID, name = "", normalizedName = "").updated()

    fun newGenre(): Genre = Genre(uuid = TestConstants.UUID, name = "").updated()

    fun newRequest(): ChangeGenreRequest = ChangeGenreRequest(name = "Name")

    fun assertDomainGenresDeepEquals(expected: List<...>, actual: List<...>) { /* size check + per-element assertGenreDeepEquals */ }
    fun assertGenreDeepEquals(expected: Domain?, actual: Domain?, ignoreUuid: Boolean = false) { /* assertSoftly on every field + AuditUtils.assertAuditDeepEquals */ }
    fun assertGenresDeepEquals(expected: List<Domain>, actual: List<Entity>) { /* ... */ }
    fun assertGenreDeepEquals(expected: Domain, actual: Entity, ignoreUuid: Boolean = false) { /* ... */ }
    fun assertGenreListDeepEquals(expected: List<Entity>, actual: List<Entity>) { /* ... */ }
    fun assertGenreDeepEquals(expected: Entity, actual: Entity, ignoreUuid: Boolean = false) { /* ... */ }
    fun assertRequestDeepEquals(expected: ChangeGenreRequest, actual: Domain, uuid: String) { /* id null, uuid matches param, fields match, audit fields null */ }
    fun assertFilterDeepEquals(expected: EntityFilter, actual: DomainFilter) { /* field-for-field */ }

}

// top-level extension functions, one per domain/entity type:
fun com.github.vhromada.catalog.domain.Genre.updated(): com.github.vhromada.catalog.domain.Genre {
    name = "Name"
    normalizedName = "Name"
    return this
}

fun Genre.updated(): Genre = copy(name = "Name")
```

Rules:

- `<X>_COUNT` constant — must equal the number of rows that resource has in `V99999999.0__TestData.sql` (see `flyway-migration`). Changing one without the other breaks every count assertion across
  facade/repository `*SpringTest`s for that resource.
- Deterministic, fixed UUID literals per index (`getUuid(index)`), copied verbatim from the SQL insert statements — never randomly generated at test-run time.
- `assertXDeepEquals` overload family: one pair (domain↔domain, domain↔entity, entity↔entity) per comparison direction actually used by the resource's tests, each with an `ignoreUuid: Boolean = false`
  parameter for the add/duplicate cases where a fresh UUID is generated and can't be asserted exactly.
- Top-level `fun <DomainType>.updated(): DomainType` / `fun <EntityType>.updated(): EntityType` extension functions (not members of the `XUtils` object) mutate/`.copy()` a fixture into its "updated"
  shape for update tests — domain version mutates `var` fields and returns `this`, entity version returns a `.copy(...)`.
- Reuse these builders in every new test for the resource — never hand-construct a domain/entity object inline in a test method.

## Pitfalls

- Don't write a `*FacadeTest` with Mockito mocks — facades are covered only by `*FacadeSpringTest` (see `facade-layer`).
- Don't use `@DirtiesContext` to fix id/sequence flakiness — see Testcontainers section above.
- Don't forget `verifyNoMoreInteractions`/`verifyNoInteractions` at the end of a unit or controller test — a missing one silently allows an extra unintended call to go unnoticed.
- Docker must be running for any `*SpringTest` (Testcontainers) — if the test run fails with a container-startup error, check Docker first before assuming a code bug.
- Keep `XUtils` fixtures and `V99999999.0__TestData.sql` in lockstep — a schema/test-data change without a matching `XUtils` update (or vice versa) breaks count assertions in ways that are easy to
  misdiagnose as a product bug.

## Checklist

- [ ] Unit tests (service/mapper): `MockitoExtension`, SUT in `@BeforeEach`, one test per branch, `verifyNoMoreInteractions`/ `verifyNoInteractions` on every mock in every test.
- [ ] Validator test: no extension, `assertThatThrownBy` with key + message + 422 for every failing branch.
- [ ] Controller test: `@WebMvcTest` + `@WithMockUser`, facade and `IssueMapper` as `@MockitoBean`, `csrf()` on writes, one test per endpoint.
- [ ] `*SpringTest` written only for facade and repository, not service/controller/mapper/validator.
- [ ] `*SpringTest` flushes after mutation, asserts both result and row count, uses `clearReferencedData()` if FK-referenced.
- [ ] `XUtils` builder added/extended with `<X>_COUNT`, deterministic UUIDs, full `assert*DeepEquals` family, `updated()` extensions — kept in sync with `V99999999.0__TestData.sql`.
