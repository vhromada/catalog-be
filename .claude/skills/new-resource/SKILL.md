---
name: new-resource
description: >-
  Use when adding a complete new resource to catalog-be (or a major new child/composite resource) — orchestrates the Flyway migration, domain/repository, service, controller/entity DTOs, mapper,
  validator, and facade layers plus their tests, in the right order. Also use to decide whether a new resource is a simple (Genre-like) or child (Season-like) resource before starting.
---

# New resource (orchestrator)

## Scope

Adding an entire new resource to catalog-be end-to-end — every layer from the database table up to the REST controller, plus tests. This skill sequences the eight other `.claude/skills/` layer skills;
it does not duplicate their content. Read `.claude/skills/new-resource/conventions.md` first — it's the shared reference every layer skill also links back to.

## 1. Gather the resource spec before writing anything

Pin these down (ask the user if ambiguous) before invoking any layer skill:

- **Name**: singular PascalCase (e.g. `Director`), its plural table name (`directors`), URL path (`rest/directors`).
- **Fields**: name + Kotlin type + nullability for each business field; which ones need a `normalized_<col>` companion (any text matched case/accent-insensitively via `FieldOperation.LIKE`).
- **Relationship shape**: standalone (own table, no parent) vs. child of another resource (owns a `@ManyToOne` back-reference and is reached only through the parent, like `Season` under `Show`) vs.
  composite (plain standalone but with multiple required name fields, like Movie's czech/original name) — see the decision guide below.
- **Search filter shape**: does an existing filter fit (`NameFilter`, `MultipleNameFilter`), or does it need a bespoke `entity/filter/<X>Filter.kt` + `domain/filter/<X>Filter.kt` pair?
- **Statistics**: plain count, or does `getStatistics()` need to compute something resource-specific?

### Simple (Genre-like) vs. child (Season-like) resource

|                                          | Simple                               | Child                                                                                                                       |
|------------------------------------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| Own repository/service/facade/controller | Yes                                  | Yes (service still exists, but methods are often parent-scoped, e.g. `search(show: Int, pageable)`)                         |
| Domain entity                            | No parent reference                  | Has a `@ManyToOne(fetch = LAZY)` back-reference to the parent                                                               |
| Parent domain entity                     | —                                    | Has `@OneToMany(fetch = EAGER, cascade = [ALL], orphanRemoval = true)` to the child                                         |
| `remove`/`duplicate` in service          | Plain `repository.delete/save`       | **Must** re-fetch the parent via its own repository inside the same `@Transactional` method — see `service-layer`'s pitfall |
| Reference implementation                 | Genre (every layer skill's template) | `Season`/`SeasonServiceImpl` (`service-layer` skill's pitfall section), `Episode`, `Song`, `BookItem`                       |

A composite resource (Movie/Show/Book-shaped: multiple name fields, extra validated fields like year/IMDB code) is still "simple" in the sense of not having a parent — it only changes the
`validator-layer` (reuse `ValidationSupport`) and `controller-api` (`MultipleNameFilter`) steps, not the overall sequence below.

## 2. Invoke the layer skills, in this order

Each step names the skill to invoke and why it has to happen at that point in the sequence (mostly: later layers' signatures depend on earlier layers' types).

1. **`flyway-migration`** — schema first, in a new migration file with the next major version (`V<n+1>.0__<Resource>.sql`), never in `V1.0__Catalog.sql`; nothing else compiles without the table
   existing (conceptually) and both migration files in sync.
2. **`domain-repository`** — domain entity, domain filter, repository; the thing every other main-source layer ultimately operates on or converts to/from.
3. **`service-layer`** — depends on the repository; if this is a child resource, apply the re-fetch-parent pattern now, not later.
4. **`controller-api` (entity DTOs only, not the controller yet)** — write `entity/<X>.kt`, `entity/io/Change<X>Request.kt`, `entity/<X>Statistics.kt`, and the filter, before the
   mapper/validator/facade, since all three of those layers' signatures reference these DTOs.
5. **`mapper-layer`** — needs both the domain type (step 2) and the entity DTOs (step 4).
6. **`validator-layer`** — needs the request DTO (step 4) only; independent of the mapper, can swap order with it if convenient.
7. **`facade-layer`** — needs service (3), mapper (5), and validator (6) all to exist first.
8. **`controller-api` (the controller itself)** — needs the facade (7).
9. **`layer-tests`** — test-data builder first (`utils/<X>Utils.kt`, needed by every other test), then unit tests (service, mapper), validator test, controller test, then `*SpringTest`s for facade and
   repository last (these need real data, i.e. the Flyway test-data file from step 1 and the `XUtils` builder to already agree).

## 3. Final step

Run `.\gradlew.bat build` (Docker must be running for Testcontainers). Fix any compile or test failures before reporting the resource done.

Non-blocking reminders — mention these to the user, don't block on them:

- Update AGENTS.md's resource list (the "Project" section's one-sentence description of what the API manages) if the new resource is a new top-level catalog item (movies/shows/games/...-shaped), not a
  supporting/internal resource.

## File-count sanity table

For a simple (Genre-like) resource, expect roughly:

| Area | Count | Files                                                                                                                                                                                                                                                                       |
|------|-------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Main | ~15   | migration (1), domain (1), domain filter (1, or 0 if reusing a shared shape — rare), repository (1), service interface+impl (2), facade interface+impl (2), mapper interface+impl (2), validator interface+impl (2), controller (1), entity response+request+statistics (3) |
| Test | ~7    | service test (1), mapper test (1), validator test (1), controller test (1), facade SpringTest (1), repository SpringTest (1), XUtils builder (1)                                                                                                                            |

A child resource adds parent-side domain/service/test changes on top of this; a composite resource adds no extra files but more branches inside the validator/filter files it already has.

## Pitfalls

- Don't skip straight to writing the controller before the facade exists — the sequence above exists because later steps' method signatures are derived from earlier steps' types, not an arbitrary
  preference.
- Don't forget step 4 happens **before** the mapper/validator, even though the controller itself (step 8) comes much later — the DTOs are needed early, the controller class is needed late.
- See each individual layer skill's own Pitfalls section for layer-specific mistakes (facade transactions, mapper `!!` safety, validator key naming, etc.) — this orchestrator doesn't repeat them.

## Verification

1. Each generated `SKILL.md`-style file (if you're also authoring a new skill, not just a resource) should have valid frontmatter — not relevant when just adding a resource.
2. For a new resource: diff the generated files against the layer skill's Genre-derived template section — annotations, bean names, signatures, and KDoc phrases should match the pattern exactly, with
   only resource-specific names/fields substituted.
3. `.\gradlew.bat build` green, including the new `*SpringTest`s (Docker running).
4. Optional — offer this to the user, don't do it unprompted: scaffold the new resource on a scratch branch first, run the build, and discard the branch if it was only meant as a dry run before
   committing to the real implementation.
