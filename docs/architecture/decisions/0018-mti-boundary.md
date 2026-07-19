# ADR-0018: Media Transparency Indicator boundary

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex needs a stable architectural category for media-transparency disclosure while external standards and governance continue to evolve.

## Decision

Filminex introduces the **Media Transparency Indicator (MTI)** framework. Its only current category is:

```text
MTI-001 — Deepfake Icon
```

No additional MTI category exists at this time. Specification details, certification, council governance, and final artwork are not frozen by this ADR.

## Consequences

- Runtime and domain contracts can reference a stable MTI category identifier.
- New categories require explicit approval and a superseding or additional ADR.
- Governance evolution cannot silently redefine current application behavior.
