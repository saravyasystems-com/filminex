# ADR-0012: Property cascading with provenance

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex must reuse broader production intent while allowing narrower creative control without hiding where effective values originated.

## Decision

Properties cascade through:

```text
Story → Episode → Scene → Shot → Frame
```

Every effective value exposes provenance, including whether it was inherited from a broader scope or locally overridden. Overrides do not erase their source history. Users must be able to inspect and navigate to the value's origin.

The exact resolution algorithm, storage schema, and conflict UI remain design decisions.

## Consequences

- Repetition is reduced without sacrificing explainability.
- Changes can identify affected descendants.
- Hidden or untraceable inheritance is prohibited.
- Changing the cascade or provenance requirement needs a superseding ADR.
