---
name: validator-layer
description: >-
  Use when adding or changing a validator in catalog-be — business validation of a Change<X>Request that accumulates Result<Unit>/Event and throws InputException. Covers the key-naming convention
  (_NULL/_EMPTY, "mustn't" phrasing) and reusing ValidationSupport for composite resources instead of hand-rolling czech/original-name or list-of-strings checks.
---

# Validator layer

## Scope

`validator/<X>Validator.kt` (interface) + `validator/impl/<X>ValidatorImpl.kt` — business validation of a `Change<X>Request`, called by the facade before the service is touched. This is the only layer
that uses `Result<T>`/`Event` directly (see the Result Pattern in AGENTS.md) — everywhere else, exceptions alone carry failure.

Inline convention summary (full detail below): one public method `validateRequest(request)` returning `Unit` (it throws rather than returning a result); build a `Result<Unit>()`, add an `Event` per
failing field inside a `when` block, throw `InputException(result = result)` if `result.isError()`; event keys follow `<RESOURCE>_<FIELD>_NULL`/`_EMPTY`, messages always start with the capitalized
field name and "mustn't".

## Files (Genre reference)

| File                                   | Purpose                                            |
|----------------------------------------|----------------------------------------------------|
| `validator/GenreValidator.kt`          | interface                                          |
| `validator/impl/GenreValidatorImpl.kt` | implementation                                     |
| `validator/utils/ValidationSupport.kt` | shared helpers for composite resources — see below |

## Interface

```kotlin
package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreValidator {

    /**
     * Validates request for changing genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *
     * @param request request for changing genre
     * @throws InputException if request for changing genre isn't valid
     */
    fun validateRequest(request: ChangeGenreRequest)

}
```

## Implementation

```kotlin
package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.GenreValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for genres.
 *
 * @author Vladimir Hromada
 */
@Component("genreValidator")
class GenreValidatorImpl : GenreValidator {

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

}
```

Rules:

- `@Component("<x>Validator")`; usually **no** constructor dependencies (`GenreValidatorImpl` has none) — inject a repository only if a field must be checked against stored data (e.g. uniqueness),
  which is rare.
- `validateRequest(request: Change<X>Request)` is the only public method, returns `Unit`. Internally: `val result = Result<Unit>()`, one `when { ... }` block per independently-failing field (each
  branch adds at most one `Event`, so a null name doesn't also report "empty"), then `if (result.isError()) throw InputException(result = result)` at the end — always this exact final shape.
- Event key naming: `<RESOURCE_IN_CAPS>_<FIELD_IN_CAPS>_NULL` for a null check, `..._EMPTY` for a blank/empty-string check. Message text: `"<Field, capitalized> mustn't be null."` /
  `"<Field, capitalized> mustn't be empty string."` — match this phrasing exactly, including the trailing period and "mustn't" (not "must not").
- `InputException` defaults to `HttpStatus.UNPROCESSABLE_CONTENT` (422) when constructed from a `Result`/key+message pair — don't pass a different status for a plain field-validation failure.

### Key-naming table

| Check                      | Key suffix      | Message                                                                              |
|----------------------------|-----------------|--------------------------------------------------------------------------------------|
| field is null              | `_NULL`         | `"<Field> mustn't be null."`                                                         |
| field is empty string      | `_EMPTY`        | `"<Field> mustn't be empty string."`                                                 |
| field contains null (list) | `_CONTAIN_NULL` | `"<Collection> mustn't contain null value."` (via `ValidationSupport.validateItems`) |

## Composite resources: reuse `ValidationSupport`

For a resource with a czech/original name pair (Movie, Show, Book, ...) or a list-of-strings field (subtitles, genre UUIDs, ...), do **not** hand-write the same `when` branches — call the shared
helpers in `validator/utils/ValidationSupport.kt`:

```kotlin
val result = Result<Unit>()
ValidationSupport.validateNames(czechName = request.czechName, originalName = request.originalName, prefix = "MOVIE_", result = result)
ValidationSupport.validateItems(
    items = request.genres,
    collectionKey = "MOVIE_GENRES",
    itemKey = "MOVIE_GENRE",
    collectionLabel = "Genres",
    itemLabel = "Genre",
    result = result
)
if (result.isError()) {
    throw InputException(result = result)
}
```

`validateNames` produces `<prefix>CZECH_NAME_NULL`/`_EMPTY` and `<prefix>ORIGINAL_NAME_NULL`/`_EMPTY` (note: `prefix` must already include the trailing underscore, e.g. `"MOVIE_"`). `validateItems`
produces `<collectionKey>_NULL`/`_CONTAIN_NULL` and `<itemKey>_EMPTY`. See `ShowValidatorImpl`, `MovieValidatorImpl`, `BookValidatorImpl` for full worked examples combining these with
resource-specific checks (year ranges, IMDB codes) in the same `Result<Unit>`.

## Pitfalls

- Don't return a `Result<T>` from `validateRequest` — the method signature returns `Unit` and communicates failure purely by throwing; `Result<T>` never escapes the validator layer (see the Result
  Pattern section of AGENTS.md).
- Don't add a second `Event` for the same field in one call (e.g. both NULL and EMPTY) — structure the checks as a `when` so only the first matching branch fires, matching the Genre example.
- Don't invent new message phrasing — "mustn't be null."/"mustn't be empty string." are fixed strings tests assert on verbatim (`hasMessageContaining(...)`).

## Checklist

- [ ] `@Component("<x>Validator")`, no unnecessary constructor dependencies.
- [ ] `validateRequest` builds one `Result<Unit>()`, adds one `Event` per failing field via `when`, throws `InputException(result = result)` at the end only if `result.isError()`.
- [ ] Keys/messages follow the `<RESOURCE>_<FIELD>_NULL`/`_EMPTY` + "mustn't" convention exactly.
- [ ] Composite resources reuse `ValidationSupport.validateNames`/`validateItems` instead of duplicating their logic.
- [ ] KDoc on the interface method lists every validation error as a bullet, matching what the impl actually checks.
