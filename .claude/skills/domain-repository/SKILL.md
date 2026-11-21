---
name: domain-repository
description: >-
  Use when adding or changing the domain (JPA entity) and repository layer in catalog-be — a new domain data class, a domain filter for search, or a Spring Data repository. Covers Audit extension,
  sequence-backed id generation, normalized columns, merge(), JpaFilter, and JpaSpecificationExecutor conventions.
---

# Domain + repository layer

## Scope

The JPA-mapped persistence entity (`domain/`), its search filter (`domain/filter/`), and its Spring Data repository (`repository/`) for a catalog-be resource. Distinct from `entity/`, the API-facing
DTOs (see `controller-api`).

Inline convention summary (full detail below): domain class extends `Audit()`, uses a sequence-backed id triple, pairs text fields with `normalized<Field>` columns, and exposes a `merge(...)` that
copies only mutable fields; the domain filter extends `JpaFilter<X>()`; the repository is a plain interface (no stereotype annotation) over `JpaRepository`/`JpaSpecificationExecutor` plus
`findByUuid`.

## Files (Genre reference)

| File                            | Purpose                                                                         |
|---------------------------------|---------------------------------------------------------------------------------|
| `domain/Genre.kt`               | JPA entity                                                                      |
| `domain/filter/GenreFilter.kt`  | search filter, used by `repository.findAll(filter.toSpecification(), pageable)` |
| `repository/GenreRepository.kt` | Spring Data repository                                                          |

## Domain entity conventions

```kotlin
package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents genre.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "genres")
@Suppress("JpaDataSourceORMInspection")
data class Genre(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "genre_generator", sequenceName = "genres_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    @Column(name = "genre_name")
    var name: String,

    /**
     * Normalized name
     */
    @Column(name = "normalized_genre_name")
    var normalizedName: String

) : Audit() {

    /**
     * Merges genre.
     *
     * @param genre genre
     */
    fun merge(genre: Genre) {
        name = genre.name
        normalizedName = genre.normalizedName
    }

}
```

Rules:

- `data class <X>(...)` extending `Audit()` (gives `createdUser`/`createdTime`/`updatedUser`/`updatedTime`, all nullable, mapped in `domain/Audit.kt` — never redeclare these).
- `@Entity @Table(name = "<table>") @Suppress("JpaDataSourceORMInspection")` on the class (the suppress silences the IDE's "no datasource configured" inspection — keep it, it's not a mistake to
  remove).
- `id` is always first, `var id: Int?`, with the generator triple: `@Id`, `@SequenceGenerator(name = "<x>_generator", sequenceName = "<table>_sq", allocationSize = 1)`,
  `@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "<x>_generator")`.
- `uuid` is always second, `val uuid: String` (immutable — uuid never changes after creation).
- Business fields are `var` (mutable, so `merge`/test `updated()` helpers can change them in place), `@Column(name = "...")` **only** when the column name differs from the decapitalized Kotlin
  property name (e.g. `name` → `genre_name`); omit `@Column` when they match.
- Every text field that has a `normalized_<col>` column in the schema gets a matching `var normalized<Field>: String` property, same `@Column` treatment.
- `fun merge(<x>: <X>)` — copies every **mutable** business field (never `id`, `uuid`, or the audit fields) from the argument onto `this`. Used by the facade's update flow (see `facade-layer`).
- For relationships, follow the fetch/cascade conventions already used by the parent resource you're extending (e.g. `Show.seasons` is
  `@OneToMany(fetch = EAGER, cascade = [ALL], orphanRemoval = true)`) — and see the facade-layer pitfall about re-fetching across `@Transactional` boundaries before writing a child resource's service.

## Domain filter conventions

```kotlin
package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Genre
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for genres.
 *
 * @author Vladimir Hromada
 */
data class GenreFilter(

    /**
     * Name
     */
    val name: String? = null

) : JpaFilter<Genre>() {

    override fun isEmpty(): Boolean {
        return name.isNullOrBlank()
    }

    override fun process(root: Root<Genre>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "name", name, FieldOperation.LIKE)?.let { result.add(it) }
        return result.toList()
    }

}
```

- Every field is a nullable `val` defaulting to `null`.
- `isEmpty()` — true when every field is null/blank; this gates `JpaFilter.assertNotEmpty()`/`toSpecification()` (throws `InputException("EMPTY_FILTER", ...)` if called while empty — the service layer
  checks `isEmpty()` itself before calling `toSpecification()`, see `service-layer`).
- `process(...)` builds one `Predicate` per field via the protected `getPredicate(criteriaBuilder, root, path, field, operation)` helper from `JpaFilter`: pass `FieldOperation.LIKE` for
  case-insensitive substring search on text, omit `operation` (defaults to `EQ`) for exact match. `getPredicate` returns `null` when the field is blank, so `?.let { result.add(it) }` is the standard
  one-liner per field.
- `JpaFilter<T>` (in `domain/filter/JpaFilter.kt`) is the shared abstract base — don't reimplement `toSpecification()`/`assertNotEmpty()`.

## Repository conventions

```kotlin
package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreRepository : JpaRepository<Genre, Int>, JpaSpecificationExecutor<Genre> {

    /**
     * Finds genre by UUID.
     *
     * @param uuid UUID
     * @return genre
     */
    fun findByUuid(uuid: String): Optional<Genre>

}
```

- Plain interface, **no** stereotype annotation (not even `@Repository`) — Spring Data JPA creates the bean itself.
- Extends `JpaRepository<<X>, Int>` (Int id — never a different id type) **and** `JpaSpecificationExecutor<<X>>` (needed for `findAll(filter.toSpecification(), pageable)` in the service layer).
- `fun findByUuid(uuid: String): Optional<<X>>` is mandatory on every resource repository — the service's `get(uuid)` depends on it.
- A child resource's repository (e.g. `SeasonRepository`) additionally gets a `findAllByXId(id: Int, pageable: Pageable)`-shaped query method for scoping to its parent; see `SeasonRepository` for the
  pattern when scaffolding a child resource.

## Pitfalls

- Don't put `@Column` on a property whose name already matches the column — only annotate where they differ.
- Don't make `id` or `uuid` part of `merge()` — merge only touches fields the update request can actually change.
- Don't add `@Repository`/`@Component` to the repository interface — this breaks the project's bean-naming convention check by being inconsistent with every other repository, and is simply unnecessary
  with Spring Data JPA.

## Checklist

- [ ] Domain class extends `Audit()`, has `@Entity @Table @Suppress("JpaDataSourceORMInspection")`.
- [ ] Id triple with matching generator/sequence names; uuid is `val`; business fields `var`.
- [ ] Every searched text field has a `normalized<Field>` counterpart.
- [ ] `merge(...)` copies only mutable business fields.
- [ ] Domain filter extends `JpaFilter<X>()`, implements `isEmpty()` and `process(...)`.
- [ ] Repository is annotation-free, extends `JpaRepository<X, Int>` + `JpaSpecificationExecutor<X>`, has `findByUuid`.
- [ ] KDoc on the class and every constructor property/method, ending `@author Vladimir Hromada`.
