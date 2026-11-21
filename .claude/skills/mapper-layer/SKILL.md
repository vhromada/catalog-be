---
name: mapper-layer
description: >-
  Use when adding or changing a mapper in catalog-be — hand-written conversion between a domain entity and its entity DTOs (mapX/mapXs/mapRequest/mapFilter). Covers the NormalizerService +
  UuidProvider injection, the single `source` parameter convention, and the fully-qualified-vs-imported import rule for the two Genre classes sharing a name.
---

# Mapper layer

## Scope

`mapper/<X>Mapper.kt` (interface) + `mapper/impl/<X>MapperImpl.kt` — converts between `domain` entities and `entity` DTOs, and between `entity.filter` and `domain.filter`. Hand-written, **not**
generated — this project does not use MapStruct or any mapping library.

Inline convention summary (full detail below): every method's single parameter is always named `source`; `mapRequest` is the only method that creates new identity (uuid) and derived data (normalized
fields), and may safely `!!` fields the validator has already checked; the domain and entity `Genre` classes share a simple name, so imports distinguish them by qualifying one fully.

## Files (Genre reference)

| File                             | Purpose        |
|----------------------------------|----------------|
| `mapper/GenreMapper.kt`          | interface      |
| `mapper/impl/GenreMapperImpl.kt` | implementation |

## Interface

```kotlin
package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest

/**
 * An interface represents mapper for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreMapper {

    /**
     * Maps genre.
     *
     * @param source genre
     * @return mapped genre
     */
    fun mapGenre(source: Genre): com.github.vhromada.catalog.entity.Genre

    /**
     * Maps list of genres.
     *
     * @param source list of genres
     * @return mapped list of genres
     */
    fun mapGenres(source: List<Genre>): List<com.github.vhromada.catalog.entity.Genre>

    /**
     * Maps request for changing genre.
     *
     * @param source request for changing genre
     * @return mapped genre
     */
    fun mapRequest(source: ChangeGenreRequest): Genre

    /**
     * Maps filter for genres.
     *
     * @param source filter for name
     * @return mapped filter for genres
     */
    fun mapFilter(source: NameFilter): GenreFilter

}
```

## Implementation

```kotlin
package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.mapper.GenreMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for genres.
 *
 * @author Vladimir Hromada
 */
@Component("genreMapper")
class GenreMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : GenreMapper {

    override fun mapGenre(source: Genre): com.github.vhromada.catalog.entity.Genre {
        return com.github.vhromada.catalog.entity.Genre(
            uuid = source.uuid,
            name = source.name
        )
    }

    override fun mapGenres(source: List<Genre>): List<com.github.vhromada.catalog.entity.Genre> {
        return source.map { mapGenre(source = it) }
    }

    override fun mapRequest(source: ChangeGenreRequest): Genre {
        return Genre(
            id = null,
            uuid = uuidProvider.getUuid(),
            name = source.name!!,
            normalizedName = normalizerService.normalize(source = source.name)
        )
    }

    override fun mapFilter(source: NameFilter): GenreFilter {
        return GenreFilter(name = source.name)
    }

}
```

Rules:

- `@Component("<x>Mapper")`; constructor-inject `NormalizerService` and `UuidProvider` even if a given resource's mapper only needs one of them (omit the unused one, don't inject what you don't call).
- Every mapping method takes exactly one parameter, always named `source`, regardless of its type — this is what lets callers write `mapper.mapGenre(source = ...)` uniformly.
- `mapX(source: Domain): Entity` / `mapXs(source: List<Domain>): List<Entity>` — `mapXs` is always a one-line `source.map { mapX(source = it) }`, never hand-rolled.
- `mapRequest(source: ChangeXRequest): Domain` is the only place that: generates a fresh `uuid` via `uuidProvider.getUuid()`, sets `id = null`, and computes
  `normalized<Field> = normalizerService.normalize(source = source.<field>)`. It is safe to `!!` a request field here (`source.name!!`) **only** because the facade always runs the validator before
  calling the mapper — never add a null check in the mapper itself; that duplicates the validator's job and masks a facade wiring bug instead of surfacing it.
- `mapFilter(source: EntityFilter): DomainFilter` — one-line field-for-field copy, no normalization (search matches against the `normalized_<col>` column via `FieldOperation.LIKE` in the domain filter
  itself, see `domain-repository`).
- Import convention: the `domain` type is imported normally (`import com.github.vhromada.catalog.domain.Genre`) and referenced bare; the `entity` type of the **same simple name** is referenced
  fully-qualified inline (`com.github.vhromada.catalog.entity.Genre(...)`) rather than imported, to avoid a naming collision. This only matters when both classes share a simple name (as `Genre` does)
  — when they don't collide, import both normally.

## Pitfalls

- Don't add validation logic (null checks, empty checks) in the mapper — that belongs exclusively in the `validator` layer; the mapper's `!!` on request fields is intentionally unguarded and relies on
  validation having already run.
- Don't let `mapXs` diverge from `source.map { mapX(source = it) }` — if you need different behavior for a list, that's a sign the single-item mapper is wrong, not that the list mapper needs new
  logic.
- Don't normalize anything outside of `mapRequest` — `mapX` (domain → entity) never recomputes normalized fields, it just drops them (they don't appear in the entity DTO at all).

## Checklist

- [ ] `@Component("<x>Mapper")`, constructor injection of only the collaborators actually used.
- [ ] All parameters named `source`.
- [ ] `mapXs` is `source.map { mapX(source = it) }`.
- [ ] `mapRequest` sets `id = null`, generates `uuid`, computes every `normalized<Field>`.
- [ ] No validation/null-safety logic beyond the request-field `!!` in `mapRequest`.
- [ ] Same-simple-name domain/entity classes disambiguated via one fully-qualified reference, not two imports.
