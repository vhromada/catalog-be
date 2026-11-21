---
name: facade-layer
description: >-
  Use when adding or changing the facade layer in catalog-be — the interface + impl pair that orchestrates validate → map → service → map over entity DTOs
  (search/get/add/update/remove/duplicate/getStatistics). Covers the update-as-get-then-merge-then-store flow, sort built in the facade, and why facades have no unit tests.
---

# Facade layer

## Scope

`facade/<X>Facade.kt` (interface) + `facade/impl/<X>FacadeImpl.kt` — orchestrates one use case per method: runs the `validator`, calls the `service`, converts between `entity` and `domain` via the
`mapper`. Operates on `entity` DTOs at its boundary, `domain` objects internally through the service.

Inline convention summary (full detail below): `@Component`, not `@Transactional` (the service owns transactions); `update` is always get-then-merge-then-store, never a raw replace; `search` builds
the `Sort` itself and hands it to `filter.toPageable(sort)`; facades are covered only by `*FacadeSpringTest` integration tests, never a Mockito unit test.

## Files (Genre reference)

| File                             | Purpose        |
|----------------------------------|----------------|
| `facade/GenreFacade.kt`          | interface      |
| `facade/impl/GenreFacadeImpl.kt` | implementation |

## Interface

```kotlin
package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreFacade {

    /**
     * Returns page of genres for filter.
     *
     * @param filter filter
     * @return page of genres for filter
     */
    fun search(filter: NameFilter): Page<Genre>

    /**
     * Returns genre.
     *
     * @param uuid UUID
     * @return genre
     * @throws InputException if genre doesn't exist in data storage
     */
    fun get(uuid: String): Genre

    /**
     * Adds genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *
     * @param request request for changing genre
     * @return created genre
     * @throws InputException if request for changing genre isn't valid
     */
    fun add(request: ChangeGenreRequest): Genre

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
     * @throws InputException if request for changing genre isn't valid
     */
    fun update(uuid: String, request: ChangeGenreRequest): Genre

    /**
     * Removes genre.
     *
     * @param uuid UUID
     * @throws InputException if genre doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated genre
     * @throws InputException if genre doesn't exist in data storage
     */
    fun duplicate(uuid: String): Genre

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): GenreStatistics

}
```

## Implementation

```kotlin
package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.GenreFacade
import com.github.vhromada.catalog.mapper.GenreMapper
import com.github.vhromada.catalog.service.GenreService
import com.github.vhromada.catalog.validator.GenreValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for genres.
 *
 * @author Vladimir Hromada
 */
@Component("genreFacade")
class GenreFacadeImpl(

    /**
     * Service for genres
     */
    private val service: GenreService,

    /**
     * Mapper for genres
     */
    private val mapper: GenreMapper,

    /**
     * Validator for genres
     */
    private val validator: GenreValidator

) : GenreFacade {

    override fun search(filter: NameFilter): Page<Genre> {
        val genres = service.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedName", "id")))
        return Page(data = mapper.mapGenres(source = genres.content), page = genres)
    }

    override fun get(uuid: String): Genre {
        return mapper.mapGenre(source = service.get(uuid = uuid))
    }

    override fun add(request: ChangeGenreRequest): Genre {
        validator.validateRequest(request = request)
        return mapper.mapGenre(source = service.store(genre = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeGenreRequest): Genre {
        validator.validateRequest(request = request)
        val genre = service.get(uuid = uuid)
        genre.merge(genre = mapper.mapRequest(source = request))
        return mapper.mapGenre(source = service.store(genre = genre))
    }

    override fun remove(uuid: String) {
        service.remove(genre = service.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Genre {
        return mapper.mapGenre(source = service.duplicate(genre = service.get(uuid = uuid)))
    }

    override fun getStatistics(): GenreStatistics {
        return GenreStatistics(count = service.getCount().toInt())
    }

}
```

Rules:

- `@Component("<x>Facade")`; constructor-injects the service, mapper, and validator (always this order: service, mapper, validator).
- `search`: `mapper.mapFilter(source = filter)` to get the domain filter, `filter.toPageable(sort = Sort.by(...))` for paging — the `Sort` is built **here**, in the facade, not in the service or
  repository. Genre sorts by `Sort.by("normalizedName", "id")` (normalized field first for stable alphabetic order, `id` as a tiebreaker). Wrap the service's `Page<Domain>` result:
  `Page(data = mapper.mapXs(source = <result>.content), page = <result>)` using the two-arg `entity.paging.Page` constructor that derives `PagingInfo` from the Spring `Page`.
- `get`: `mapper.mapX(source = service.get(uuid = uuid))` — one line, nothing else.
- `add`: validate, then `mapper.mapX(source = service.store(x = mapper.mapRequest(source = request)))` — validate always comes first.
- `update`: validate → `service.get(uuid = uuid)` to fetch the current domain object → `<domain>.merge(<x> = mapper.mapRequest(source = request))` to apply the request's fields in place →
  `mapper.mapX(source = service.store(x = <domain>))`. **Never** build a brand-new domain object for update — always get-then-merge-then-store, so unmapped fields (id, uuid, audit) survive untouched.
- `remove`: `service.remove(x = service.get(uuid = uuid))` — fetch-then-remove by domain object, not a bare `removeByUuid`.
- `duplicate`: `mapper.mapX(source = service.duplicate(x = service.get(uuid = uuid)))`.
- `getStatistics`: builds the statistics DTO directly from `service.getCount()` (or whatever counts the service exposes) — no mapper involved, since statistics has no domain-side counterpart.

## Pitfalls

- **No `@Transactional` on the facade.** Transaction boundaries live in the service layer only (`@Transactional` on `store`/`remove`/`duplicate`); adding it here would create a second, outer
  transaction and defeat the single-session assumption the child-resource re-fetch pattern in `service-layer` depends on.
- **Facades have no unit tests.** Because every method is a thin orchestration of three collaborators with no branching logic of its own, there is no `*FacadeTest` with Mockito mocks — coverage comes
  exclusively from `*FacadeSpringTest` (see `layer-tests`). Don't create a Mockito-based facade test; it would just re-assert the orchestration order with mocks and add nothing beyond what the Spring
  integration test already proves against a real database.
- Don't skip the validator on `update` — both `add` and `update` validate before touching the service, even though `update` also needs a `get` to find the row to merge into.

## Checklist

- [ ] `@Component("<x>Facade")`, constructor order service → mapper → validator.
- [ ] `search` builds its own `Sort`, maps filter and content explicitly.
- [ ] `add`/`update` call `validator.validateRequest` before any service call.
- [ ] `update` is get → merge → store, never a fresh domain object.
- [ ] `remove`/`duplicate` fetch via `service.get(uuid = uuid)` first.
- [ ] No `@Transactional` anywhere in the facade.
- [ ] No Mockito-based `*FacadeTest` created — only `*FacadeSpringTest` is expected.
