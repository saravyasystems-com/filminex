# Sprint 0 — Platform Foundation

**Status:** In progress  
**Started:** 18 July 2026  
**Genesis baseline:** [Filminex Genesis](../../genesis/README.md)

## Sprint goal

Create a runnable, testable platform skeleton that proves the declared Filminex boundaries:

- Spring Boot application foundation;
- PostgreSQL persistence foundation;
- workspace and identity boundary;
- Logging Engine foundation;
- Capability Engine foundation;
- provider-agnostic AI Engine with a Grok adapter boundary;
- architecture enforcement and delivery automation.

Sprint 0 establishes foundations. It does not implement filmmaking feature depth.

## Deliverables

### Architecture

- System context and container architecture
- Module and dependency architecture
- Deployment and environment architecture
- Security, tenancy, and authorization architecture
- Persistence and asset-storage architecture
- Observability and audit architecture
- AI provider architecture
- Architecture decision records

### Software design

- Package/module map
- core interfaces and dependency rules
- workspace, identity, capability, logging, and AI models
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

1. Approve architecture and repository layout through ADRs.
2. Scaffold the application and module boundaries.
3. Add persistence, migrations, and local development infrastructure.
4. Implement identity/workspace seams.
5. Implement logging and capability foundations.
6. Implement the AI Engine port and provider adapter boundary.
7. Add CI, architecture tests, and operational documentation.
8. Run the Sprint 0 exit review.

## Exit criteria

- Application builds and starts locally.
- Health checks pass.
- PostgreSQL schema initializes through versioned migrations.
- Module dependency rules are automatically tested.
- Workspace-scoped authorization has an enforceable seam.
- Audit events can be recorded.
- Capabilities can be evaluated independently of UI visibility.
- AI calls can only pass through the AI Engine interface.
- Grok-specific code is isolated in its adapter.
- CI verifies build, tests, formatting, and architecture rules.
- Architecture and software-design documents match the implemented system.

## Out of scope

- Full story editor
- production libraries
- reverse-population workflows
- billing-provider integration
- direct editing/3D-tool integration
- finished production UI
