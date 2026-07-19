# ADR-0023: ADR governance

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex separates creative brainstorming from repository-controlled engineering decisions and requires a durable architectural history.

## Decision

ADR lifecycle:

```text
Proposed → Accepted → Frozen → Superseded
```

- **Proposed:** under review.
- **Accepted:** approved direction that may still mature before freezing.
- **Frozen:** architectural baseline; change requires a superseding ADR.
- **Superseded:** retained historically and replaced by a named later ADR.

The repository is the engineering source of truth. Brainstorm conversations never automatically become architecture. Every future architectural change references the ADR it supersedes.

## Consequences

- Frozen ADRs are never silently rewritten.
- ADR indexes record status and supersession.
- Unresolved implementation, legal, governance, provider, version, and schema questions remain outside frozen decisions.
