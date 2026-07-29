# Solr Infrastructure

**Status:** Implemented foundation
**Workstream:** Sprint 0 — 4/14
**Decisions:** [ADR-0007](decisions/0007-search-architecture.md), [ADR-0008](decisions/0008-event-driven-indexing.md)

## Boundary

PostgreSQL remains authoritative. Solr contains only disposable search projections that
reference stable Filminex IDs. Business modules call the `MediaSearch` API and never use
Solr-specific request or response types.

The initial projection supports workspace-scoped discovery across titles, media
descriptions, subtitles, transcripts, dubbing text, and extensible string attributes.
Every query requires a workspace ID so one tenant's projection is not returned to another.

## Runtime

Local development and CI use Apache Solr 9.8.1 with a single `filminex` core. The local
container uses a persistent Docker volume; CI starts with a clean projection. The
application reads the endpoint and timeouts from environment variables:

| Variable | Default |
|---|---|
| `FILMINEX_SOLR_URL` | `http://localhost:8983/solr/filminex` |
| `FILMINEX_SOLR_CONNECT_TIMEOUT` | `2s` |
| `FILMINEX_SOLR_READ_TIMEOUT` | `5s` |

`/actuator/health` reports Solr readiness separately from PostgreSQL health.

## Reliability contract

- Indexing is idempotent because stable document IDs replace their prior projection.
- Delete operations propagate using the same stable IDs.
- Solr failure never rolls back authoritative PostgreSQL state.
- A blank core can be rebuilt from authoritative records.
- Search may be eventually consistent.

The outbox, retry worker, replay command, and full rebuild orchestration belong to
Workstream 5. SolrCloud, semantic/vector search, and production topology remain out of
Sprint 0.
