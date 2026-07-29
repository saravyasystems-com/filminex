# Modular Monolith Dependency Design

**Status:** Implemented baseline
**Workstream:** Sprint 0 — 1/14
**Decision:** [ADR-0004](decisions/0004-modular-monolith.md)

## Packaging contract

Filminex is one deployable Spring Boot application divided into capability modules beneath:

`com.saravyasystems.filminex.<module>`

Each module follows this contract:

- `api` contains the stable types other modules may use.
- `internal` contains implementation, persistence, adapter, and orchestration types.
- Internal implementation types are package-private.
- A module may depend on another module only through the target module's `api`.
- The root application package is composition/bootstrap code and is not a domain module.

These rules are executable in `ModularMonolithArchitectureTests`. The regular Gradle
`test` task runs them, so the GitHub Actions build fails when a rule is violated.

## Modules and public boundaries

| Module | Public boundary | Responsibility |
|---|---|---|
| `identity` | `IdentityService` | Workspace-scoped identity |
| `projects` | `ProjectService` | Project lifecycle |
| `story` | `StoryService` | Story hierarchy |
| `production` | `ProductionService` | Scene production planning |
| `characters` | `CharacterDirectory` | Persistent character identity |
| `locations` | `LocationDirectory` | Reusable locations |
| `wardrobe` | `WardrobeCatalog` | Wardrobe and ornaments |
| `collaboration` | `CollaborationService` | Comments, reviews, controlled sharing |
| `knowledge` | `KnowledgeService` | Knowledge and provenance resolution |
| `capabilities` | `CapabilityService` | Capabilities and entitlements |
| `audit` | `AuditService` | Business audit history |
| `ai` | `AiService` | Provider-neutral AI tasks |
| `assets` | `ObjectStorage` | Media-binary storage abstraction |
| `search` | `MediaSearch` | Rebuildable search abstraction |
| `localization` | `LocalizationService` | Subtitles, transcripts, and dubbing |
| `rights` | `TalentRightsProvider` | Replaceable talent-rights decisions |
| `transparency` | `MediaTransparencyService` | MTI decisions and disclosures |

The interface names are the initial Sprint 0 software-design vocabulary. They can evolve
compatibly until a later contract is published; the frozen architectural rule is that
cross-module access occurs through stable public interfaces.

## Allowed dependency graph

An omitted edge is forbidden. Transitive access does not grant direct access.

| Module | May depend on |
|---|---|
| `identity` | `audit` |
| `projects` | `identity` |
| `story` | `projects`, `characters`, `locations`, `wardrobe`, `knowledge`, `audit` |
| `production` | `story`, `characters`, `locations`, `wardrobe`, `assets`, `localization`, `rights`, `transparency`, `audit` |
| `characters` | `assets`, `audit` |
| `locations` | `assets`, `audit` |
| `wardrobe` | `assets`, `audit` |
| `collaboration` | `projects`, `identity`, `audit` |
| `knowledge` | `characters`, `locations`, `wardrobe`, `audit` |
| `capabilities` | `identity`, `audit` |
| `audit` | none |
| `ai` | `capabilities`, `assets`, `audit` |
| `assets` | `audit` |
| `search` | `assets`, `localization` |
| `localization` | `characters`, `assets`, `rights` |
| `rights` | `identity`, `audit` |
| `transparency` | `rights`, `audit` |

## Enforcement

Architecture tests verify:

1. code outside a module cannot depend on that module's `internal` packages;
2. implementation types under `internal` are not public;
3. cross-module dependencies target only an allowed module;
4. cross-module dependencies target only the target module's `api`;
5. the dependency graph has no cycles;
6. the dependency declaration covers every registered module.

Adding a module requires updating both this document and the executable module registry in
the architecture test.
