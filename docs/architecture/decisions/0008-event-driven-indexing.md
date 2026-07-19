# ADR-0008: Event-driven indexing

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Search projections must follow authoritative state changes without coupling database transactions directly to Solr availability.

## Decision

Search indexing is asynchronous:

```text
PostgreSQL
    ↓
Domain Event
    ↓
Index Worker
    ↓
Solr
```

The indexing flow provides retries, delete propagation, rebuild, and idempotency. A failed index update does not roll back a committed authoritative transaction. The exact broker, outbox design, worker library, and delivery timing remain implementation decisions.

## Consequences

- Search is explicitly eventually consistent.
- Indexing failures require observability and recovery.
- A blank index can be reconstructed.
- Replacing asynchronous event-driven indexing requires a superseding ADR.
