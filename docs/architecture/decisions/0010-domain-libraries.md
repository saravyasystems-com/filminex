# ADR-0010: Domain libraries

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex needs reusable production knowledge shared across stories, scenes, and projects without repeated unstructured prompts.

## Decision

Filminex provides reusable global libraries for:

- Characters
- Locations
- Wardrobe
- Camera
- Lighting
- Props
- Expressions
- Motions
- VFX
- Music

Library entries have stable identity and may be referenced by production contexts. Their exact schemas and editing experiences remain software-design work.

## Consequences

- Production contexts reference reusable knowledge instead of duplicating identity.
- Contextual overrides do not mutate the global source silently.
- Removing or redefining these canonical library areas requires a superseding ADR.
