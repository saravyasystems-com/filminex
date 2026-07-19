# ADR-0005: Persistence architecture

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex requires an authoritative transactional record distinct from media binaries and search projections.

## Decision

**PostgreSQL is the authoritative source of truth.**

It stores authoritative state and metadata for projects, stories and production hierarchy, media, characters, locations, collaboration, localization, rights references, and other application records.

Schema changes use versioned migrations. The exact PostgreSQL version, persistence library, table design, and migration details remain implementation decisions.

## Consequences

- Derived systems must be reconstructable from PostgreSQL and other authoritative stores.
- Solr never becomes authoritative.
- Media binaries are excluded by ADR-0006.
- Changing the authoritative database role requires a superseding ADR.
