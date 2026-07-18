# ADR-0005: PostgreSQL persistence and migration strategy

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex needs authoritative transactional state for workspaces, production knowledge, provenance, entitlements, rights decisions, asset metadata, and reliable integration events.

## Decision

- PostgreSQL is the authoritative system of record for application metadata and business state.
- Every schema change is delivered through ordered, immutable Flyway migrations.
- Each capability owns its tables and persistence adapters even when one physical database is used.
- Foreign keys, unique constraints, and transaction boundaries protect invariants at the database level where appropriate.
- Store a transactional outbox record with state changes that must reach Solr or another asynchronous adapter.
- Do not store media binaries in PostgreSQL.
- ORM/query technology may vary behind a capability's persistence port; no domain model depends on it.

## Consequences

- PostgreSQL can reconstruct all derived indexes and projections.
- Migrations become production artifacts and require forward/backward compatibility planning.
- Cross-capability reporting uses documented read models rather than ownership-breaking table access.
- Database backup alone does not back up media binaries; asset recovery is governed by ADR-0006.

## Verification

- A clean database initializes entirely from migrations.
- Migration tests run in CI against PostgreSQL.
- Outbox creation is atomic with the authoritative state change.
