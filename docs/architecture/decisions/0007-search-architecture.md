# ADR-0007: Search architecture

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex requires brisk discovery across media and related structured and localized metadata without overloading the authoritative database.

## Decision

**Apache Solr** is the search engine.

Rules:

- Solr is not a source of truth.
- PostgreSQL remains authoritative.
- Solr stores searchable projections referencing stable Filminex IDs.
- Business modules use a search abstraction rather than Solr-specific APIs.
- Sprint 0 includes Solr integration, indexing, basic searching, and a rebuild process.
- Version policy is a stable Apache Solr 9.x release.

Exact Solr version, client mechanism, schema internals, and production topology remain implementation decisions. Semantic/vector search and SolrCloud are excluded from Sprint 0.

## Consequences

- Search may be eventually consistent.
- Solr loss must not cause authoritative data loss.
- Replacing Solr requires a superseding ADR.
