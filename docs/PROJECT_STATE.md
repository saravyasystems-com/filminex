# Filminex Project State

**Updated:** 31 July 2026
**Delivery phase:** Sprint 0 — Platform Foundation  
**Genesis:** Frozen and closed

## Completed

- Genesis product and platform baseline
- Documentation-first repository foundation
- Java 25, Spring Boot 4.1.x, and Gradle 9.6.x baseline
- Modular-monolith repository layout
- Initial `filminex-api` application, health configuration, test, wrapper, and CI workflow
- Workstream 1 module boundaries and architecture tests, verified by CI and merged through PR #1
- Workstream 2 PostgreSQL and Flyway foundation, verified by CI and merged through PR #2
- Workstream 3 object-storage abstraction, verified by CI and merged through PR #3
- Workstream 4 Solr infrastructure, verified by CI and merged through PR #4
- Workstream 5 event and outbox foundation, verified by CI and merged through PR #5
- Workstream 6 workspace and identity foundation, verified by CI and merged through PR #6
- Workstream 7 logging and audit foundation, verified by CI and merged through PR #7
- Workstream 8 capabilities and entitlements foundation, verified by CI and merged through PR #8
- Workstream 9 provider-agnostic AI Engine foundation, verified by CI and merged through PR #9
- Workstream 10 rights-provider foundation, verified by CI and merged through PR #10
- Workstream 11 localization foundation, verified by CI and merged through PR #11
- Workstream 12 media-transparency foundation, verified by CI and merged through PR #12
- Workstream 13 frontend foundation, verified by CI and merged through PR #13

## Frozen architectural baseline

- PostgreSQL is authoritative application state.
- Media binaries live in versioned object storage.
- Solr is a non-authoritative, rebuildable search projection.
- Identity and authorization are workspace scoped behind provider-neutral seams.
- Audit/provenance records are distinct from diagnostic logs.
- AI providers are adapters behind the AI Engine port; Grok is initial configuration.
- A local rights provider is used now; FTRP is a separate future Saravya project.
- Localization is first class and connects dialogue, scenes, talent/characters, voice profiles, assets, rights, search, and export.
- MTI currently has one category, MTI-001 Deepfake Icon, limited to AI/hybrid synthetic or materially altered human likeness or voice.

The frozen baseline is recorded in ADR-0001 through ADR-0019, ADR-0022, and ADR-0023. ADR-0020 and ADR-0021 remain Accepted because governance and intellectual-property strategy are still evolving.

## Current implementation direction

- Backend: Java/Spring Boot with Gradle; exact versions remain implementation policy rather than frozen product architecture.
- Frontend: React, TypeScript, and Vite.
- Authentication: provider-neutral seam with a lightweight local-development identity adapter; external provider selection is deferred.

## Sprint 0 exit candidate

- The React/TypeScript/Vite frontend shell is verified by CI and merged.
- Workspace and identity persistence, membership roles, and authorization seam are operational.
- The AI Engine foundation is verified by CI and merged through Sprint 0 Workstream 9.
- The rights-provider foundation is verified by CI and merged through Sprint 0 Workstream 10.
- The MTI foundation is verified by CI and merged through Sprint 0 Workstream 12.
- Architecture and software-design foundation documents match the implemented boundaries.
- Workstream 14 must pass CI and be accepted before Sprint 0 is closed.
- MTI governance and IP workstream documents are not yet written.

## Deferred

- Full filmmaking workspaces and production libraries begin after the foundation.
- FTRP consent/governance/audit/provenance is not implemented inside Filminex.
- MTCouncil establishment, government proposals, and final IP/patent decisions are separate governance work.

## Next delivery slice

Run Workstream 14 CI, review the exit evidence, and close Sprint 0 after acceptance.
