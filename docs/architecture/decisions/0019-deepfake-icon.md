# ADR-0019: Deepfake Icon applicability and behavior

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

MTI-001 requires a narrow, predictable scope that avoids multiple audience-facing AI badges.

## Decision

The Deepfake Icon applies only when all relevant conditions are met:

- AI or Hybrid production
- A real human likeness or voice
- Synthetic creation or material alteration

Behavior:

- Images receive an icon.
- Videos receive an icon.
- Audio receives metadata injection.

No other transparency badge currently exists. Final icon artwork, placement measurements, metadata format, and certification remain outside this frozen ADR.

## Consequences

- Landscapes, props, backgrounds, and unrelated AI use do not trigger MTI-001.
- AI providers and export adapters may not suppress an applicable decision.
- Changing scope or media behavior requires a superseding ADR.
