# Media Transparency Foundation

**Status:** Implemented foundation
**Workstream:** Sprint 0 — 12/14
**Decisions:** [ADR-0018](decisions/0018-mti-boundary.md), [ADR-0019](decisions/0019-deepfake-icon.md)

## Boundary

`MediaTransparencyService` derives an explainable transparency decision from
workspace-scoped production facts and converts that decision into an export instruction.
It does not allow a caller or provider adapter to suppress an applicable instruction.

The only supported category is `MTI-001 — Deepfake Icon`. It applies when all three
conditions hold:

1. production mode is AI or Hybrid;
2. the media uses a real human likeness or voice; and
3. that likeness or voice is synthetic or materially altered.

Images and videos require an icon instruction. Audio requires a metadata-injection
instruction. A non-applicable result explicitly returns `NONE` and an explainable reason.

## Evidence and audit

Requests carry stable media, workspace, correlation, and provenance references. Every
evaluation appends a workspace-scoped audit event containing the category, media kind,
decision, and reason. Evidence references remain part of the returned decision so an
export pipeline can retain provenance without coupling MTI to an asset format.

## Deferred

- final Deepfake Icon artwork and placement measurements;
- concrete media rendering and metadata-writing adapters;
- certification and conformance testing;
- Media Transparency Council governance;
- new MTI categories;
- legal or intellectual-property conclusions.

These items cannot silently alter runtime applicability. Any scope change requires an
approved architecture decision.
