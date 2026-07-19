# ADR-0013: Reverse data population

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex must convert existing creative material into structured, reviewable production knowledge rather than require all properties to be entered manually.

## Decision

Filminex supports reverse population from scripts, character images, and location images. Import context determines interpretation:

- Character import proposes character properties.
- Location import proposes location properties.
- Scene import proposes scene understanding.
- Script import proposes the production hierarchy and related knowledge.

AI-generated or inferred values are proposals with provenance and require review. Exact models, confidence thresholds, and generation workflows remain future implementation details.

## Consequences

- The same source may be interpreted differently by its declared context.
- Automation cannot silently become authoritative.
- Removing context-dependent reverse population requires a superseding ADR.
