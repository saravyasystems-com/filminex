# ADR-0013: Media Transparency Indicator integration boundary

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex needs a narrow, explainable disclosure mechanism for synthetic or materially altered human media. Governance, global standardization, MTCouncil, and intellectual-property strategy are broader workstreams and must not be conflated with runtime integration.

## Decision

- Use the fixed term **Media Transparency Indicator (MTI)**.
- Define exactly one current category: **MTI-001: Deepfake Icon**.
- MTI-001 applies only in AI or hybrid mode when a real human likeness or voice is synthetically created or materially altered.
- It does not apply to landscapes, locations, props, backgrounds, animation without governed human likeness/voice, or physical production merely because Filminex was used.
- Image output receives a visible icon; video output receives a visible icon; audio output receives metadata disclosure.
- A provider-neutral MTI evaluator consumes production mode, human-media involvement, transformation facts, and rights/provenance references, then returns an explainable disclosure decision.
- Export/render adapters apply the decision. AI adapters cannot suppress it.
- Record the MTI category/version, decision basis, application result, and output asset version in audit/provenance records.
- The icon is intended to be globally acceptable, soothing, and non-proprietary to Filminex or FTRP.

## Consequences

- Runtime behavior stays narrow while the specification can mature independently.
- No additional badge/category may be introduced without specification and ADR updates.
- Audio relies on metadata rather than a visible mark.
- Interoperable metadata format, icon artwork, placement rules, MTCouncil charter, government proposals, and IP/patent strategy require governance documents.

## Deferred

- Final icon design and placement measurements.
- External standardization and government adoption.
- Final legal, trademark, defensive-publication, and patent conclusions.
