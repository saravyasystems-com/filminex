# Rights Provider Foundation

**Status:** Implemented foundation
**Workstream:** Sprint 0 — 10/14
**Decision:** [ADR-0015](decisions/0015-talent-rights-boundary.md)

## Boundary

Filminex asks `TalentRightsProvider` for operational talent-rights decisions. The request
identifies the workspace, requesting user, talent, intended use, production mode,
territory, intended time, and correlation identity. It contains no future FTRP domain
objects or external-provider SDK types.

Decisions are explainable and use one of three states:

- `ALLOWED` when an active local grant matches use, territory, and time;
- `DENIED` when a relevant grant is revoked or expired; or
- `REVIEW_REQUIRED` when no matching grant exists.

Absence of evidence never silently becomes consent.

## Local provider

`LocalTalentRightsProvider` supplies the deliberately limited Sprint 0 implementation.
Workspace administrators create and revoke grants. Workspace members may evaluate and
list rights for their workspace. Every query includes the workspace boundary.

Each grant preserves a stable talent identifier, permitted uses, territories, validity
window, evidence reference, lifecycle state, and administrator provenance. Evidence
references are locators; consent documents and media bytes do not live in this table.

## Audit

Grant creation, revocation, and every decision append WS7 audit evidence. Audit details
contain identifiers, provider, use, decision, and reason. They exclude consent content and
other potentially sensitive evidence.

## Deferred

- external FTRP protocol and provider adapter;
- talent-facing consent and approval workflows;
- contractual restrictions, compensation, exclusivity, and revocation governance;
- jurisdiction-specific policy interpretation;
- evidence-document storage and signature verification; and
- automatic propagation into production, localization, and MTI workflows.
