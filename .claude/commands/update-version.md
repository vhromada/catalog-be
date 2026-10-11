---
description: Update the application version in all places it is defined
argument-hint: <version, e.g. 26.5 or 26.5-SNAPSHOT>
allowed-tools: Read, Edit, Grep, Glob, Bash(git diff:*), Bash(git status:*)
---

Update the application version of Catalog BE to the new version `$ARGUMENTS`.

## 1. Validate the input

The new version is required and must match `^\d+\.\d+(-SNAPSHOT)?$` (e.g. `26.5` or `26.5-SNAPSHOT`).
If `$ARGUMENTS` is empty or does not match, stop and ask the user for a valid version. Do not change anything.

Derive two values:
- `full` — the argument exactly as given (may contain the `-SNAPSHOT` suffix).
- `plain` — the argument without the `-SNAPSHOT` suffix.

## 2. Find the current version

Read the current `version = "..."` from `build.gradle.kts`. Strip a `-SNAPSHOT` suffix from it to get `old-plain`, which is used for searching in step 4.

## 3. Edit the files

| File                                                                     | Change                                                | Uses                     |
|--------------------------------------------------------------------------|-------------------------------------------------------|--------------------------|
| `build.gradle.kts`                                                       | `version = "<full>"`                                  | `full` (with suffix)     |
| `compose.yaml`                                                           | `image: 'catalog-be:<full>'` of the `catalog` service | `full` (with suffix)     |
| `src/main/kotlin/com/github/vhromada/catalog/CatalogWebConfiguration.kt` | `.version("<plain>")` in `catalogInfo()`              | `plain` (without suffix) |

Do not touch any other version string (plugin, library or image versions such as `postgres:18.4`).

## 4. Report

Show `git diff` and summarize what changed. Do not commit — the version is usually committed together with the dependency update.
