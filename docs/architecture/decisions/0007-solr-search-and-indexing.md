# ADR-0007: Solr search and event-driven indexing

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex requires brisk discovery across large media libraries, structured filmmaking properties, subtitles, transcripts, dubbing, provenance, and relationships. PostgreSQL remains authoritative, while object storage contains media binaries.

## Decision

- Use Apache Solr as a non-authoritative, rebuildable search projection.
- Provide full-text search, filtering, faceting, autocomplete-ready fields, and semantic/vector fields where a validated use case exists.
- A Search port shields the application and Knowledge Engine from Solr APIs and schema details.
- Authoritative changes create outbox events in PostgreSQL. An idempotent indexing worker consumes them and upserts or removes Solr documents.
- Index records carry authoritative entity ID, workspace ID, entity/version type, source version, visibility fields, searchable metadata, and localization text.
- Query-time authorization filters are mandatory; indexed content never widens access.
- Support replay from the outbox and full rebuild from PostgreSQL plus authoritative asset/localization metadata.
- Search results contain identifiers and projections; authoritative detail is resolved from application state.

## Consequences

- Search can be eventually consistent.
- Solr failure degrades discovery but not authoritative writes.
- Schema evolution requires compatible indexing and rebuild procedures.
- Semantic search does not make Solr an AI source of truth.

## Verification

- Duplicate events are harmless.
- Failed events retry and become observable without blocking authoritative transactions.
- A blank Solr collection can be rebuilt.
- Workspace isolation is tested in every search path.
