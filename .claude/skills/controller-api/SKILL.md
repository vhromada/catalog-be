---
name: controller-api
description: >-
  Use when adding or changing the controller and API entity DTOs in catalog-be — a new @RestController, request/response/filter/statistics DTOs under entity/, or REST endpoint wiring. Covers the
  standard endpoint/status table, thin delegation to a facade, and the nullable-request-vs-non-null-response DTO split.
---

# Controller + entity DTOs

## Scope

`controller/<X>Controller.kt` plus its `entity/` DTOs: the response (`entity/<X>.kt`), the change request (`entity/io/Change<X>Request.kt`), statistics (`entity/<X>Statistics.kt`), and the search
filter (`entity/filter/<X>Filter.kt` or a reused shared filter).

Inline convention summary (full detail below): controller is thin (one-line delegation to the facade, no logic, no `ResponseEntity`); standard six/seven-endpoint shape with fixed HTTP statuses;
response DTOs are all non-null, request DTOs are all nullable with no bean-validation annotations (validation happens in the `validator` layer instead).

## Files (Genre reference)

| File                                  | Purpose                                                                |
|---------------------------------------|------------------------------------------------------------------------|
| `controller/GenreController.kt`       | controller                                                             |
| `entity/Genre.kt`                     | response DTO                                                           |
| `entity/io/ChangeGenreRequest.kt`     | request DTO                                                            |
| `entity/GenreStatistics.kt`           | statistics DTO                                                         |
| `entity/filter/NameFilter.kt`         | search filter (reused — Genre's filter is just a name)                 |
| `entity/filter/PagingFilter.kt`       | base class every filter extends, for `page`/`limit`/`toPageable(sort)` |
| `entity/filter/MultipleNameFilter.kt` | reusable czech/original-name filter for composite resources            |
| `entity/paging/Page.kt`               | generic response wrapper for `search`                                  |

## Controller

```kotlin
package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.GenreFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for genres.
 *
 * @author Vladimir Hromada
 */
@RestController("genreController")
@RequestMapping("rest/genres")
@Tag(name = "Genres")
class GenreController(

    /**
     * Facade for genres
     */
    private val facade: GenreFacade

) {

    /**
     * Returns page of genres for filter.
     *
     * @param filter filter
     * @return page of genres for filter
     */
    @GetMapping
    fun search(filter: NameFilter): Page<Genre> {
        return facade.search(filter = filter)
    }

    /**
     * Returns genre.
     * <br></br>
     * Validation errors:
     *
     *  * Genre doesn't exist in data storage
     *
     * @param uuid UUID
     * @return genre
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable uuid: String): Genre {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *
     * @param request request for changing genre
     * @return added genre
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeGenreRequest): Genre {
        return facade.add(request = request)
    }

    /**
     * Updates genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Genre doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing genre
     * @return updated genre
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable uuid: String,
        @RequestBody request: ChangeGenreRequest
    ): Genre {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes genre.
     * <br></br>
     * Validation errors:
     *
     *  * Genre doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates genre.
     * <br></br>
     * Validation errors:
     *
     *  * Genre doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated genre
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable uuid: String): Genre {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): GenreStatistics {
        return facade.getStatistics()
    }

}
```

### Endpoint/status table

| Method                      | Mapping                            | Status                                       | Notes                                                                 |
|-----------------------------|------------------------------------|----------------------------------------------|-----------------------------------------------------------------------|
| `search(filter: <X>Filter)` | `@GetMapping`                      | 200 (default)                                | filter bound as plain query params, no `@RequestParam` wrapper object |
| `get(uuid)`                 | `@GetMapping("{uuid}")`            | 200                                          | `@PathVariable uuid: String`                                          |
| `add(request)`              | `@PostMapping`                     | 201 `@ResponseStatus(HttpStatus.CREATED)`    | `@RequestBody`                                                        |
| `update(uuid, request)`     | `@PutMapping("{uuid}")`            | 200 (default)                                | two params on separate lines when both present                        |
| `remove(uuid)`              | `@DeleteMapping("{uuid}")`         | 204 `@ResponseStatus(HttpStatus.NO_CONTENT)` | returns `Unit`                                                        |
| `duplicate(uuid)`           | `@PostMapping("{uuid}/duplicate")` | 201 `@ResponseStatus(HttpStatus.CREATED)`    |                                                                       |
| `getStatistics()`           | `@GetMapping("statistics")`        | 200 (default)                                |                                                                       |

Rules:

- `@RestController("<x>Controller") @RequestMapping("rest/<xs>") @Tag(name = "<Xs>")` — plural lower path, plural capitalized tag.
- Controller body is **one flat package** `controller/` (no subpackages), thin: every method is a single-line delegation to the facade with named arguments, no `ResponseEntity<T>`, no business logic,
  no try/catch (exceptions propagate to `RestExceptionHandler`).
- KDoc on every method: summary line, then (when the facade call can throw) a `<br></br>` + `Validation errors:` bullet list mirroring the facade/validator's actual error conditions, then
  `@param`/`@return`.

## Entity DTOs

### Response (`entity/Genre.kt`) — all fields non-null

```kotlin
package com.github.vhromada.catalog.entity

/**
 * A class represents genre.
 *
 * @author Vladimir Hromada
 */
data class Genre(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    val name: String

)
```

No `id`, no audit fields — the API never exposes the internal integer id or audit metadata, only `uuid` plus business fields.

### Request (`entity/io/ChangeGenreRequest.kt`) — all fields nullable, no bean-validation annotations

```kotlin
package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing genre.
 *
 * @author Vladimir Hromada
 */
data class ChangeGenreRequest(

    /**
     * Name
     */
    val name: String?

)
```

Every field is nullable (`String?`, not `String`) even when conceptually required — "required" is enforced by the `validator` layer at runtime, not by Kotlin's type system or `jakarta.validation`
annotations. Never add `@NotNull`/`@NotBlank` etc. here.

### Statistics (`entity/GenreStatistics.kt`)

```kotlin
package com.github.vhromada.catalog.entity

/**
 * A class represents statistics for genres.
 *
 * @author Vladimir Hromada
 */
class GenreStatistics(

    /**
     * Count of genres
     */
    val count: Int

)
```

Plain class (not `data class`) with a `count: Int` at minimum; add more fields only if the facade's `getStatistics()` genuinely computes more than a count.

### Filters

Extend `entity/filter/PagingFilter.kt` (gives `var page: Int?`, `var limit: Int?`, `fun toPageable(sort: Sort?)`). Reuse an existing filter shape before writing a new one:

- `NameFilter(name: String? = null)` — single name field (Genre, Author's simpler siblings).
- `MultipleNameFilter(czechName: String? = null, originalName: String? = null)` — composite czech/original name resources (Movie, Show, Book, ...).

Only write a bespoke `entity/filter/<X>Filter.kt` when neither shared filter fits the resource's searchable fields.

## Security note

Only `rest/public/**` and `/app`, `/app/*` are unauthenticated (see `CatalogSecurityConfiguration.kt`). A new controller mapped under `rest/<xs>` requires HTTP Basic auth by default — that's correct
for almost every resource; flag it to the user explicitly if a new endpoint is meant to be public, since that's the unusual case.

## Checklist

- [ ] `@RestController("<x>Controller")`, `@RequestMapping("rest/<xs>")`, `@Tag(name = "<Xs>")`.
- [ ] All seven standard methods present (or the subset that applies), each a one-line facade delegation with named arguments.
- [ ] Statuses match the table: 201 on add/duplicate, 204 on remove, 200 (default) elsewhere.
- [ ] Response DTO fields all non-null, no `id`/audit fields.
- [ ] Request DTO fields all nullable, zero bean-validation annotations.
- [ ] Filter extends `PagingFilter` (reused `NameFilter`/`MultipleNameFilter` when it fits).
- [ ] KDoc with validation-errors bullet list on methods that can throw.
