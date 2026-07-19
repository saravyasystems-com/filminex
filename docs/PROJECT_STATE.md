# Filminex Project State

**Updated:** 19 July 2026  
**Delivery phase:** Sprint 0 — Platform Foundation  
**Genesis:** Frozen and closed

## Completed

- Genesis product and platform baseline
- Documentation-first repository foundation
- Java 25, Spring Boot 4.1.x, and Gradle 9.6.x baseline
- Modular-monolith repository layout
- Initial `filminex-api` application, health configuration, test, wrapper, and CI workflow

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

## Current gaps

- Successful Java 25 CI/build execution has not yet been recorded as Sprint evidence.
- The React/TypeScript/Vite frontend has not yet been scaffolded or verified.
- Platform module boundaries and architecture tests are not implemented.
- PostgreSQL, Flyway, object storage, Solr, outbox/indexing worker, and local infrastructure are not implemented.
- Identity, capability, audit, AI, rights, localization, and MTI contracts are not implemented.
- Architecture and software-design documents remain incomplete as listed in the Sprint 0 register.
- MTI governance and IP workstream documents are not yet written.

## Deferred

- Full filmmaking workspaces and production libraries begin after the foundation.
- FTRP consent/governance/audit/provenance is not implemented inside Filminex.
- MTCouncil establishment, government proposals, and final IP/patent decisions are separate governance work.

## Next delivery slice

Implement modular-boundary enforcement, PostgreSQL/Flyway, asset metadata, Solr local infrastructure, and the reliable indexing outbox. Record build and test evidence with the same change.
