# Event and Outbox Foundation

**Status:** Implemented
**Workstream:** Sprint 0 — 5/14
**Decision:** [ADR-0008](decisions/0008-event-driven-indexing.md)

## Purpose

Filminex persists a domain event in PostgreSQL within the same transaction as the
authoritative state change. A background dispatcher delivers committed events to
idempotent handlers. External systems such as Solr therefore cannot make an
authoritative database transaction fail.

## Delivery flow

```text
Authoritative transaction
    -> event_outbox (PostgreSQL)
    -> batch claim with FOR UPDATE SKIP LOCKED
    -> matching event handler
    -> PROCESSED or scheduled retry
```

The outbox is append-only from the publisher's perspective. Delivery metadata records
attempt count, the next eligible attempt, completion time, and the latest error.

## Guarantees

- An event is committed or rolled back with its authoritative transaction.
- Concurrent workers cannot claim the same pending batch.
- Delivery is at least once; handlers must be idempotent.
- Failed delivery never deletes the event and is retried after configurable backoff.
- Events are workspace scoped and retain aggregate identity and occurrence time.
- Unknown event types remain pending and visible rather than being silently discarded.
- Search upsert and delete events use a public audit contract; Solr details remain
  private to the search module.

## Search event contracts

- `search.document.upserted.v1` indexes a complete search projection.
- `search.document.deleted.v1` removes a projection by document ID.

Replaying either event is safe because Solr upserts and deletes are idempotent.
Full projection rebuild orchestration will read authoritative records and publish the
same versioned contracts; it does not make the outbox or Solr authoritative.

## Configuration

| Setting | Default | Environment variable |
|---|---:|---|
| Batch size | 50 | `FILMINEX_OUTBOX_BATCH_SIZE` |
| Poll interval | 1 second | `FILMINEX_OUTBOX_POLL_INTERVAL` |
| Retry delay | 30 seconds | `FILMINEX_OUTBOX_RETRY_DELAY` |

Production observability, retention, poison-event policy, and multi-node worker tuning
remain deployment concerns. The schema and public contracts do not require a message
broker and do not prevent adding one later.
