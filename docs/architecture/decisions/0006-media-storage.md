# ADR-0006: Media storage

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Images, video, audio, documents, subtitles, dubbed audio, thumbnails, and derivatives are unsuitable as primary relational-database payloads.

## Decision

Binary media resides in **Object Storage**. PostgreSQL stores authoritative metadata, stable identifiers, versions, checksums, ownership, provenance, and object references.

The object-storage vendor, SDK, bucket topology, retention policy, and transfer mechanism remain implementation decisions.

## Consequences

- Asset records and binary objects require coordinated lifecycle and recovery.
- Derived assets reference their source asset and version.
- Search indexes metadata and extracted text, not binaries.
- Replacing object storage as the binary-media architecture requires a superseding ADR.
