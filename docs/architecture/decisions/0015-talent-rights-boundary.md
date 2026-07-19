# ADR-0015: Talent rights boundary

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex requires operational rights decisions while the future Film Talent Rights Platform (FTRP) remains a separate project.

## Decision

Filminex integrates through `TalentRightsProvider`.

Initial implementation:

```text
LocalTalentRightsProvider
```

Future implementation may use an FTRP provider. Filminex never embeds the FTRP domain or governance platform. Stable talent, media, decision, and evidence references preserve future integration.

The detailed local consent schema, scheduling model, external provider protocol, and full FTRP governance remain unresolved or future work.

## Consequences

- Filminex can operate before FTRP exists.
- Local operational rights logic remains deliberately limited.
- Embedding FTRP into Filminex requires a superseding ADR.
