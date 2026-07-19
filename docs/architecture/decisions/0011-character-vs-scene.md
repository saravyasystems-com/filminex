# ADR-0011: Character identity versus scene presentation

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Character consistency requires separation between persistent identity and contextual appearance or direction.

## Decision

A Character represents persistent identity, including face, physique, age, voice, identity properties, distinguishing marks, and mannerisms/director knowledge.

A Scene owns contextual presentation, including wardrobe, ornaments, hairstyle presentation, expression, lighting, location, weather, camera, and production mode.

Character identity is referenced rather than duplicated across scenes. Scene-specific choices do not overwrite the global character identity.

## Consequences

- Character consistency remains reusable across production contexts.
- Wardrobe and appearance continuity can be managed per scene.
- Exact property schemas remain design work.
- Moving contextual presentation into persistent identity requires a superseding ADR.
