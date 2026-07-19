# ADR-0022: Sprint 0 scope

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Sprint 0 must prove the Filminex foundation without expanding into complete future products or feature depth.

## Decision

Sprint 0 delivers:

- Runnable backend and frontend
- PostgreSQL and Flyway
- Object Storage seam
- Solr proof with event-driven indexing and rebuild
- AI-provider abstraction
- `TalentRightsProvider` seam
- Localization seam
- MTI architecture seam
- CI build
- ADR documentation, Project State, and Roadmap

Excluded:

- FTRP
- Billing implementation
- Full collaboration
- Semantic search
- Advanced reverse population
- MTI certification
- Full dubbing studio

## Consequences

- Sprint 0 cannot close without executable evidence for its included foundation.
- Excluded work remains visible in the Roadmap rather than entering the sprint informally.
- Scope changes require a superseding ADR.
