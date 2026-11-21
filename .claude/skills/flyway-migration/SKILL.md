---
name: flyway-migration
description: >-
  Use when adding or changing database schema in catalog-be — creating a table for a new resource, adding/renaming a column, adding a constraint or index, or any other Flyway migration. Covers the SQL
  conventions (sequence, id/uuid/normalized columns, audit columns, named constraints) and keeping the Flyway test-data file in sync.
---

# Flyway migration

## Scope

Schema changes for catalog-be's PostgreSQL database, applied via Flyway. Covers: creating a table for a new resource, adding a join table, adding/renaming a column, adding a constraint or index.

Inline convention summary (full detail below): new schema changes are a **new** `V<version>__<description>.sql` file, never an edit to `V1.0__Catalog.sql`; every table gets an INTEGER sequence-backed
id, a VARCHAR uuid with a uniqueness constraint, and `normalized_<col>` companions — 10 characters wider than their base column — for any text searched case/accent-insensitively; every change to the
main migration needs a matching change to the test-data migration.

## Files

| What                                                               | Where                                                                      |
|--------------------------------------------------------------------|----------------------------------------------------------------------------|
| Baseline schema (never edit it)                                    | `src/main/resources/db/migration/V1.0__Catalog.sql`                        |
| New resource (major) / schema fix (minor)                          | `src/main/resources/db/migration/V<version>__<description>.sql` (new file) |
| Flyway-managed test data (must stay in sync with schema)           | `src/test/resources/db/migration/V99999999.0__TestData.sql`                |

## Conventions

- `V1.0__Catalog.sql` is the baseline schema. Do not edit it, nor any other already-applied migration. A schema change is always a **new** file named `V<version>__<description>.sql`.
- Versioning:
  - **New resource** (new table(s), plus any FK column added to a parent table): **new major version** — the next major after the current highest migration, minor `0` (e.g. `V2.0__Guide.sql`,
    then `V3.0__...`). One file per new resource, containing its sequence, table, indexes and the `ALTER TABLE <parent> ADD COLUMN ...` FK if it is linked to an existing table.
  - **Fix or change of existing schema** (column type/width, constraint, index, added/renamed column): **new minor version** of the current major (e.g. `V2.1__Fix_guide_content.sql`; precedent:
    `V1.1__Normalized_columns_length.sql`).
- Table name: plural snake_case (`genres`, `movie_items`).
- Sequence: `CREATE SEQUENCE IF NOT EXISTS <table>_sq START WITH 1 INCREMENT BY 1;`
- Table: `CREATE TABLE IF NOT EXISTS <table> (...)`.
  - `id INTEGER NOT NULL CONSTRAINT <table>_pk PRIMARY KEY` — always first column.
  - `uuid VARCHAR(128) NOT NULL CONSTRAINT <table>_uuid_ck CHECK (LENGTH(uuid) > 0)` — always second column.
  - One column per business field, named `<table_singular>_<field>` when the column name would otherwise collide or be ambiguous (Genre uses `genre_name`, not bare `name`), each with a named length
    check constraint `<table>_<col>_ck CHECK (LENGTH(<col>) > 0)` for required text fields.
  - For every text field that is searched case/accent-insensitively, add a paired `normalized_<col>` column — same type, same constraints, but **10 characters wider** than `<col>` (e.g.
    `genre_name VARCHAR(200)` → `normalized_genre_name VARCHAR(210)`), since normalization (accent/diacritic stripping, case folding, ...) can grow a string's character count — plus
    `CREATE INDEX idx_<table>_normalized_<col> ON <table>(normalized_<col>);` after the table. See `V1.1__Normalized_columns_length.sql`, which had to retrofit this +10 width onto every
    pre-existing resource's `normalized_<col>` columns — give a new resource the correct width from its first migration instead of needing a similar follow-up fix later.
  - Four audit columns at the end, always `NOT NULL`, always this order: `created_user VARCHAR(36) NOT NULL`, `created_time TIMESTAMP NOT NULL`, `updated_user VARCHAR(36) NOT NULL`,
    `updated_time TIMESTAMP NOT NULL`.
  - `CONSTRAINT <table>_uuid_uk UNIQUE (uuid)` as the last line inside the table, before the closing `)`.
- Join tables (many-to-many, e.g. `movie_genres`) have **no** id, uuid, normalized, or audit columns — just the two FK columns, each with a named FK constraint `<join_table>_<side>_fk`, referencing
  the parent tables' `id`.

### Template (derived from the real `genres` table, `V1.0__Catalog.sql` lines 24-36, with the `normalized_genre_name` width corrected per `V1.1__Normalized_columns_length.sql`)

```sql
CREATE SEQUENCE IF NOT EXISTS genres_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS genres (
  id                    INTEGER      NOT NULL CONSTRAINT genres_pk PRIMARY KEY,
  uuid                  VARCHAR(128) NOT NULL CONSTRAINT genres_uuid_ck CHECK (LENGTH(uuid) > 0),
  genre_name            VARCHAR(200) NOT NULL CONSTRAINT genres_genre_name_ck CHECK (LENGTH(genre_name) > 0),
  normalized_genre_name VARCHAR(210) NOT NULL CONSTRAINT genres_normalized_genre_name_ck CHECK (LENGTH(normalized_genre_name) > 0),
  created_user          VARCHAR(36)  NOT NULL,
  created_time          TIMESTAMP    NOT NULL,
  updated_user          VARCHAR(36)  NOT NULL,
  updated_time          TIMESTAMP    NOT NULL,
  CONSTRAINT genres_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_genres_normalized_genre_name ON genres(normalized_genre_name);
```

Join table template (from `movie_genres`):

```sql
CREATE TABLE IF NOT EXISTS movie_genres (
  movie INTEGER NOT NULL CONSTRAINT movie_genres_movie_fk REFERENCES movies (id),
  genre INTEGER NOT NULL CONSTRAINT movie_genres_genre_fk REFERENCES genres (id)
);
```

### Test data

Every table change needs a matching change to `V99999999.0__TestData.sql`, which Flyway also runs against the Testcontainers database. The test-data builder (see the `layer-tests` skill) and this
file must describe the same data.

**For a brand-new resource's table, insert exactly 3 rows** — regardless of how many an older resource happens to have (e.g. `GenreUtils.GENRES_COUNT = 4` predates this convention; don't copy its
count). 3 is enough to exercise paging (a page of 1 plus a remainder) without the padding of a larger fixture set. The matching `<X>Utils.kt` constant (`<X>_COUNT`) must equal 3 as well.

```sql
INSERT INTO genres (id, uuid, genre_name, normalized_genre_name, created_user, created_time, updated_user, updated_time)
VALUES (nextval('genres_sq'), '859dd213-d9f9-4223-a5a8-77abf5c7f4ca', 'Genre 1 name', 'Genre 1 name',
        'd53b2577-a3de-4df7-a8dd-2e6d9e5c1014', '2020-01-01 00:00:00', '0998ab47-0d27-4538-b551-ee7a471cfcf1', '2020-01-02 00:00:00');
```

Follow the same `id`/uuid/audit pattern as the existing rows for other resources in that file when adding a brand-new table — reuse the same `created_user`/`updated_user` account UUIDs already used
elsewhere in the file for consistency, and generate 3 distinct, deterministic UUID literals (one per row).

## Pitfalls

- Never edit `V1.0__Catalog.sql` directly for a schema change — always a new versioned file.
- Forgetting the `V99999999.0__TestData.sql` update breaks every `*SpringTest` for that resource (wrong row counts, missing fixtures) — always update it in the same change.
- Use `CREATE TABLE IF NOT EXISTS` / `CREATE SEQUENCE IF NOT EXISTS` even in a new migration file, matching existing style.
- Don't size a `normalized_<col>` column the same as its base `<col>` — it must be **10 characters wider**. `V1.1__Normalized_columns_length.sql` exists solely to retrofit this onto every resource
  that launched without it (`VARCHAR(200)` → `VARCHAR(210)`, `VARCHAR(100)` → `VARCHAR(110)`); don't make a new resource need the same kind of follow-up fix.
- Don't copy an older resource's test-data row count (e.g. Genre's 4) for a new resource — a brand-new resource's table always gets exactly 3 fixture rows.

## Checklist

- [ ] New file named `V<version>__<description>.sql`, not an edit to `V1.0__Catalog.sql`: next **major** (`V<n+1>.0`) for a new resource, next **minor** (`V<n>.<m+1>`) for a fix/change.
- [ ] Sequence + table with id/uuid/business columns/normalized columns/audit columns/uuid unique constraint, in that order.
- [ ] Named constraints follow `<table>_pk`, `<table>_uuid_ck`, `<table>_<col>_ck`, `<table>_uuid_uk`.
- [ ] Every `normalized_<col>` column is 10 characters wider than its base `<col>` (e.g. `VARCHAR(210)` for a `VARCHAR(200)` business column).
- [ ] Normalized-column index named `idx_<table>_normalized_<col>`.
- [ ] `V99999999.0__TestData.sql` updated with matching fixture rows — exactly 3 rows for a brand-new resource's table.
