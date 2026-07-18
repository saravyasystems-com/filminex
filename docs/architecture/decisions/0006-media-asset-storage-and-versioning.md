# ADR-0006: Media asset storage and versioning

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex manages large images, video, audio, scripts, references, subtitles, and generated outputs. These require durable versioning without turning the relational database or Solr into binary stores.

## Decision

- Store media binaries in S3-compatible object storage behind an `AssetStore` port.
- Store asset identity, ownership, media type, size, checksum, object key, version, provenance, lifecycle state, and timestamps in PostgreSQL.
- Use immutable object keys per asset version. Replacement creates a new version; it never silently overwrites historical bytes.
- Verify content checksums during ingestion and retrieval where risk warrants it.
- Access private assets through authorized application flows and short-lived signed transfers.
- Deletion is a lifecycle transition with retention and legal-hold seams; physical purge is asynchronous and auditable.
- Solr indexes searchable metadata and extracted text only, never authoritative asset metadata or binaries.

## Consequences

- Asset metadata and bytes require coordinated backup and reconciliation.
- Version history supports reproducibility, provenance, and rollback.
- Local development may use an S3-compatible emulator without changing domain contracts.
- Derived thumbnails, transcripts, and proxies reference their source asset/version.

## Verification

- Uploads cannot become visible without committed metadata.
- Checksum mismatch fails ingestion.
- Asset-version history remains immutable.
