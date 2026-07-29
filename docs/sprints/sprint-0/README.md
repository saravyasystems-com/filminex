# Sprint 0 — Platform Foundation

**Status:** In progress  
**Started:** 18 July 2026  
**Genesis baseline:** [Filminex Genesis](../../genesis/README.md)

## Sprint goal

Create a runnable, testable platform skeleton that proves the declared Filminex boundaries:

- Spring Boot application foundation;
- React, TypeScript, and Vite frontend foundation;
- PostgreSQL persistence foundation;
- workspace and identity boundary;
- Logging Engine foundation;
- Capability Engine foundation;
- provider-agnostic AI Engine with a Grok adapter boundary;
- PostgreSQL as the authoritative system of record;
- versioned object storage for media assets;
- Apache Solr as a rebuildable media and localization search index;
- a first-class Localization boundary for subtitles, dubbing, and transcripts;
- a local rights-provider seam that can later be replaced by FTRP;
- the Media Transparency Indicator integration boundary;
- architecture enforcement and delivery automation.

Authentication remains provider neutral. Sprint 0 supplies a lightweight local-development identity adapter without freezing an external identity provider.

Sprint 0 establishes foundations. It does not implement filmmaking feature depth.

## Deliverables

### Architecture

- System context and container architecture
- Module and dependency architecture
- Deployment and environment architecture
- Security, tenancy, and authorization architecture
- Persistence and asset-storage architecture
- Search and indexing architecture
- Localization architecture
- Observability and audit architecture
- AI provider architecture
- Rights-provider architecture
- Media-transparency integration architecture
- Architecture decision records

### Software design

- Package/module map
- core interfaces and dependency rules
- workspace, identity, capability, logging, AI, search, rights, and localization models
- initial database schema and migrations
- API conventions and error model
- event and audit record design
- configuration and secret boundaries
- test strategy and architecture tests

### Engineering foundation

- Build and local-development setup
- application health endpoint
- database migration pipeline
- CI checks
- formatting and static analysis
- test harness
- secure configuration examples

## Work sequence

1. Reconcile the Sprint 0 plan, ADR register, and project state.
2. Approve the remaining platform-foundation ADRs.
3. Enforce modular boundaries in code and architecture tests.
4. Add PostgreSQL, Flyway, object storage, Solr, and local development infrastructure.
5. Implement event-driven indexing with replay and full rebuild support.
6. Implement workspace/identity, logging/audit, and capability seams.
7. Implement AI and rights-provider ports with local adapters.
8. Establish Localization contracts and searchable subtitle/dub/transcript fields.
9. Establish MTI evaluation and export-disclosure contracts; keep governance specifications separate.
10. Verify CI, health, migrations, architecture rules, and operational documentation.
11. Run the Sprint 0 exit review.

## Workstream status

| Workstream | Status | Evidence |
|---|---|---|
| 1/14 — Modular Monolith Foundation | Complete | Public module APIs, hidden implementation package, architecture tests, [dependency graph](../../architecture/module-dependencies.md), and green CI in PR #1 |
| 2/14 — PostgreSQL and Flyway Foundation | Complete | PostgreSQL Compose service, Flyway V1 migration, database health integration, migration test, [database design](../../architecture/postgresql-and-migrations.md), and green CI in PR #2 |
| 3/14 — Object Storage Abstraction | Complete | Vendor-neutral storage port, local filesystem adapter, checksum and traversal safeguards, lifecycle test, [storage design](../../architecture/object-storage.md), and green CI in PR #3 |
| 4/14 — Solr Infrastructure | Complete | Solr Compose/CI service, workspace-scoped search port, health indicator, lifecycle test, [Solr design](../../architecture/solr-infrastructure.md), and green CI in PR #4 |
| 5/14 — Event and Outbox Foundation | Complete | Transactional event publisher, PostgreSQL outbox, retrying dispatcher, search handlers, integration tests, [event/outbox design](../../architecture/event-outbox.md), and green CI in PR #5 |
| 6/14 — Workspace and Identity Foundation | Complete | Provider-neutral users, workspace memberships, role administration, last-admin protection, durable identity events, integration tests, [identity design](../../architecture/workspace-and-identity.md), and green CI in PR #6 |
| 7/14 — Logging and Audit Foundation | Complete | Append-only workspace audit history, actor/action/subject/outcome evidence, correlation and causation, sensitive-detail rejection, filtered queries, integration tests, [audit design](../../architecture/logging-and-audit.md), and green CI in PR #7 |
| 8/14 — Capabilities and Entitlements Foundation | Complete | Role-derived core access, provider-neutral optional entitlements, production-mode and provider decisions, workspace-policy denial, audited administration, integration tests, [capability design](../../architecture/capabilities-and-entitlements.md), and green CI in PR #8 |
| 9/14 — Provider-Agnostic AI Engine Foundation | Complete | Provider-neutral tasks and normalized results, Capability Engine enforcement, durable request audit, deterministic local adapter, isolated Grok adapter boundary, tests, [AI Engine design](../../architecture/ai-engine.md), and green CI in PR #9 |
| 10/14 — Rights Provider Foundation | Complete | Provider-neutral rights requests and explainable decisions, admin-managed local grants, workspace isolation, durable audit evidence, Flyway V6, integration tests, [rights-provider design](../../architecture/rights-provider.md), and green CI in PR #10 |
| 11/14 — Localization Foundation | Implemented; CI verification pending | Locale-aware subtitle, caption, transcript, and dub tracks; timed cues; voice profiles; reviewed searchable text; Flyway V7; integration tests; and [localization design](../../architecture/localization-foundation.md) |

## Exit criteria

- Application builds and starts locally.
- Frontend builds and starts locally.
- Health checks pass.
- PostgreSQL schema initializes through versioned migrations.
- Media binaries are stored outside PostgreSQL with immutable version metadata and checksums.
- Solr indexes are derived, replayable, and rebuildable from authoritative records.
- Subtitle, transcript, and dubbing content can enter the search projection.
- Module dependency rules are automatically tested.
- Workspace-scoped authorization has an enforceable seam.
- Audit events can be recorded.
- Capabilities can be evaluated independently of UI visibility.
- AI calls can only pass through the AI Engine interface.
- Grok-specific code is isolated in its adapter.
- Rights decisions can be supplied by the local provider without coupling Filminex to FTRP.
- MTI decisions are derived only for applicable AI/hybrid human likeness or voice use.
- CI verifies build, tests, formatting, and architecture rules.
- CI verifies both backend and frontend foundations.
- Architecture and software-design documents match the implemented system.

## Out of scope

- Full story editor
- production libraries
- reverse-population workflows
- billing-provider integration
- full FTRP consent, governance, audit, or provenance platform
- establishment of MTCouncil or government standards work
- final legal or patent determination for MTI intellectual property
- direct editing/3D-tool integration
- finished production UI
