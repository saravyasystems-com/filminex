# ADR-0004: Modular architecture enforcement

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

ADR-0002 selected a modular monolith and ADR-0003 defined capability-first boundaries. Directory names alone cannot prevent accidental coupling as identity, story, production, search, AI, rights, and localization grow.

## Decision

- Use capability-first Java packages inside the initial deployable and Gradle projects for coarse reusable boundaries.
- Each capability exposes a deliberately small public API; implementation packages are internal by convention and architecture test.
- Enforce dependency direction with automated architecture tests in CI.
- Cross-capability access uses explicit ports. For example, Story resolves character information through a `CharacterDirectory` contract rather than character persistence.
- Domain types do not depend on Spring, databases, Solr, storage SDKs, AI SDKs, or identity-provider SDKs.
- Begin with these boundaries: workspace/identity, story, production, characters, locations, wardrobe, collaboration, knowledge, search, assets, localization, capability, audit, AI, rights, media transparency, and billing seams.

## Consequences

- Coupling violations fail the build instead of becoming review conventions only.
- Some mapping between capability-owned models is intentional.
- New network services are not created merely to express module boundaries.
- Module extraction remains possible when operational evidence justifies it.

## Verification

- CI runs architecture tests.
- No adapter package is reachable from domain packages.
- No capability reads another capability's persistence implementation directly.
