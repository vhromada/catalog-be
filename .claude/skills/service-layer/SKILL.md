---
name: service-layer
description: >-
  Use when adding or changing the service layer in catalog-be — the interface + impl pair that talks to a repository and operates on domain objects (search/get/store/remove/duplicate/getCount). Covers
  the @Transactional placement, the NOT_EXIST InputException pattern, and the critical cross-call LazyInitializationException pitfall for child resources.
---

# Service layer

## Scope

`service/<X>Service.kt` (interface) + `service/impl/<X>ServiceImpl.kt` — domain logic that talks to the `repository` layer. Operates on `domain` objects only, never `entity` DTOs.

Inline convention summary (full detail below): six standard methods (search/get/store/remove/duplicate/getCount), `get` throws a `_NOT_EXIST` `InputException`, write methods are `@Transactional`,
duplicate copies with a fresh id/uuid — and a child resource's `remove`/`duplicate` must re-fetch its parent via the parent's own repository inside the same transactional method, never via the child's
lazy back-reference.

## Files (Genre reference)

| File                                | Purpose                                            |
|-------------------------------------|----------------------------------------------------|
| `service/GenreService.kt`           | interface                                          |
| `service/impl/GenreServiceImpl.kt`  | implementation                                     |
| `service/impl/SeasonServiceImpl.kt` | **reference for the child-resource pitfall below** |

## Interface

```kotlin
package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreService {

    /**
     * Returns page of genres by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of genres by filter
     */
    fun search(filter: GenreFilter, pageable: Pageable): Page<Genre>

    /**
     * Returns genre.
     *
     * @param uuid UUID
     * @return genre
     * @throws InputException if genre doesn't exist in data storage
     */
    fun get(uuid: String): Genre

    /**
     * Stores genre.
     *
     * @param genre genre
     * @return stored genre
     */
    fun store(genre: Genre): Genre

    /**
     * Removes genre.
     *
     * @param genre genre
     */
    fun remove(genre: Genre)

    /**
     * Duplicates genre.
     *
     * @param genre genre
     * @return duplicated genre
     */
    fun duplicate(genre: Genre): Genre

    /**
     * Returns count of genres.
     *
     * @return count of genres
     */
    fun getCount(): Long

}
```

## Implementation

```kotlin
package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.GenreRepository
import com.github.vhromada.catalog.service.GenreService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for genres.
 *
 * @author Vladimir Hromada
 */
@Service("genreService")
class GenreServiceImpl(

    /**
     * Repository for genres
     */
    private val repository: GenreRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : GenreService {

    override fun search(filter: GenreFilter, pageable: Pageable): Page<Genre> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Genre {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "GENRE_NOT_EXIST", message = "Genre doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(genre: Genre): Genre {
        return repository.save(genre)
    }

    @Transactional
    override fun remove(genre: Genre) {
        repository.delete(genre)
    }

    @Transactional
    override fun duplicate(genre: Genre): Genre {
        return repository.save(genre.copy(id = null, uuid = uuidProvider.getUuid()))
    }

    override fun getCount(): Long {
        return repository.count()
    }

}
```

Rules:

- `@Service("<x>Service")` bean name; constructor injection of the repository (and `UuidProvider` if `duplicate` is implemented here).
- `search`: check `filter.isEmpty()` first and call plain `repository.findAll(pageable)` — only call `filter.toSpecification()` when the filter is non-empty, since `toSpecification()` throws on an
  empty filter.
- `get`: `repository.findByUuid(uuid = uuid).orElseThrow { InputException(key = "<X>_NOT_EXIST", message = "<X> doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }` — key and message always follow
  this exact shape, substituting the resource name.
- `store`/`remove`/`duplicate` are `@Transactional`; `search`/`get`/`getCount` are **not**.
- `duplicate`: `repository.save(<x>.copy(id = null, uuid = uuidProvider.getUuid()))` for a simple resource. A composite/child resource that owns nested collections needs the pattern in **Pitfalls**
  below instead of a bare `.copy(...)`.

## CRITICAL pitfall: bidirectional relationships across Facade→Service calls (verbatim from AGENTS.md)

A `@Transactional` service method that needs to mutate a parent's `@OneToMany(fetch = EAGER, cascade = [ALL], orphanRemoval = true)` collection (e.g. `Show.seasons`, `Music.songs`) must **re-fetch the
parent fresh via its own repository inside that same method**. Never navigate from the child's `@ManyToOne(fetch = LAZY)` back-reference (`season.show`, `song.music`) if that child was itself obtained
from a separate call (e.g. a Facade's `xService.get(uuid)`), since that reference may be bound to an already-closed session, throwing `org.hibernate.LazyInitializationException`. Reading `.id` off a
lazy proxy is always safe (never triggers initialization) — use it to re-fetch: `val show = showRepository.findById(season.show!!.id!!).orElseThrow()`, then mutate `show.seasons` and `save(show)`.

Reference implementation, `service/impl/SeasonServiceImpl.kt`:

```kotlin
@Transactional
override fun remove(season: Season) {
    val show = showRepository.findById(season.show!!.id!!).orElseThrow()
    show.seasons.removeIf { it.id == season.id }
    showRepository.save(show)
}

@Transactional
override fun duplicate(season: Season): Season {
    val show = showRepository.findById(season.show!!.id!!).orElseThrow()
    val copy = season.copy(id = null, uuid = uuidProvider.getUuid(), subtitles = season.subtitles.map { it }.toMutableList(), episodes = mutableListOf(), show = show)
    copy.episodes.addAll(season.episodes.map { it.copy(id = null, uuid = uuidProvider.getUuid(), season = copy) })
    show.seasons.add(copy)
    return seasonRepository.save(copy)
}
```

The equivalent lives in `EpisodeServiceImpl`, `SongServiceImpl`, `BookItemServiceImpl` — check one of those (matching whichever parent your new child resource belongs to) when scaffolding a child
service, not just Genre.

This is why `*SpringTest` integration tests cannot be trusted alone to catch this bug: the whole test method shares one transaction/session, so a closed-session boundary never actually occurs there.
Only a separate-process E2E suite (outside this repo) exercises it. Don't skip writing the re-fetch pattern just because tests are green.

## Checklist

- [ ] Interface has the six standard methods (or the parent-scoped `search(parentId, pageable)` variant for a child resource) with full KDoc including `@throws InputException` on `get`.
- [ ] `@Service("<x>Service")` bean name, constructor injection.
- [ ] `get` throws the `<X>_NOT_EXIST` `InputException` with `HttpStatus.NOT_FOUND`.
- [ ] `store`/`remove`/`duplicate` are `@Transactional`; `search`/`get`/`getCount` are not.
- [ ] If this is a child resource: `remove`/`duplicate` re-fetch the parent via the parent's repository inside the method — never via the child's lazy back-reference.
