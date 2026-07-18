# ADR-0003: Repository structure and modular-monolith layout

- **Status:** Accepted
- **Date:** 2026-07-18
- **Decision owners:** Filminex maintainers

## Context

Filminex spans product knowledge, deployable applications, reusable domain capabilities, future user interfaces, infrastructure, and development utilities. The repository must remain understandable while the product grows and must reflect the principle **“No use. No see.”**

A premature microservice layout would multiply deployment and coordination costs. A single unstructured application would make domain boundaries and ownership difficult to preserve.

## Decision

Use a monorepo with this top-level structure:

```text
docs/                       Product, architecture, design, ADR, and sprint knowledge
apps/
  filminex-api/             First runnable Spring Boot application
packages/
  domain/                   Framework-light production knowledge model
  application/              Use cases and orchestration
  infrastructure/           Persistence and external-system adapters
  ai-provider-spi/          Provider-independent AI contracts
infra/                      Deployment and environment definitions
scripts/                    Repeatable repository utilities
```

Directories are introduced only with real content. The first runnable increment may initially contain the modules inside the `filminex-api` Gradle build; extraction into independently published packages is not required.

Within the backend, organize code by business capability first, then by technical role where needed. Initial capability boundaries are:

- workspace and membership
- story structure
- production knowledge and libraries
- property provenance and inheritance
- assets and versions
- collaboration and review
- entitlement and capability visibility
- audit and activity
- AI inference and prompt rendering

## Dependency direction

```text
delivery adapters → application → domain
infrastructure adapters → application/domain contracts
AI provider adapters → ai-provider-spi
domain → no adapter or framework module
```

Dependencies that reverse these arrows are prohibited unless recorded by a new ADR.

## Rationale

A monorepo keeps related product, architecture, API, and implementation changes reviewable together. A modular monolith minimizes operational complexity while allowing hard internal boundaries. Capability-first packaging makes the filmmaking model visible in the code and avoids large generic controller/service/repository buckets.

The structure leaves room for a web client and other deployables without inventing them before their delivery sprint.

## Consequences

### Positive

- Product vocabulary maps visibly to code ownership and module boundaries.
- One repository and initially one backend deployment simplify Sprint 0 delivery.
- Shared contracts and documentation evolve atomically.
- Capabilities can be extracted later based on scale, security, or team ownership evidence.

### Costs and constraints

- Boundary discipline requires architecture tests and code review.
- A monorepo can accumulate slow builds unless modules and CI tasks remain selective.
- Independent deployment is intentionally deferred.
- Shared libraries must not become dumping grounds.

## Guardrails

- Do not create empty placeholder directories.
- Do not introduce a new top-level directory without documenting its purpose.
- Keep generated files out of source control unless required for reproducible builds.
- Every deployable application owns its runtime configuration and health boundary.
- Shared packages contain cohesive capabilities, not miscellaneous helpers.
- Extraction to a service requires operational evidence and a dedicated ADR.
