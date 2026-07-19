# ADR-0003: Story hierarchy

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Production knowledge, navigation, inheritance, APIs, storage, and UI require one canonical narrative hierarchy.

## Decision

The canonical hierarchy is:

```text
Story
└── Episode
    └── Scene
        └── Shot
            └── Frame
```

Films may use one implicit episode where an episodic grouping is unnecessary. Takes are attempts associated with the appropriate production level; they do not replace the canonical hierarchy.

## Consequences

- Domain models, APIs, persistence, search projections, inheritance, and UI align with this hierarchy.
- Alternate organizational views may not redefine ownership.
- A structural change requires a superseding ADR.
