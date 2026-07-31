# Sprint 0 Exit Review

**Status:** Candidate for acceptance
**Baseline:** Workstream 13 merge `8b4dade`
**CI evidence:** WS13 Build #48 passed

Sprint 0 has delivered the platform foundation without adding filmmaking feature depth.
Final acceptance occurs when the WS14 pull request passes the same complete CI gate and
is merged.

## Exit matrix

| Criterion | Evidence | Result |
|---|---|---|
| Backend builds, starts, and reports health | Spring context/Actuator test with PostgreSQL and Build #48 | Pass |
| Frontend tests and production build | Vitest, strict TypeScript/Vite build, Build #48 | Pass |
| Versioned schema initializes | Flyway V1–V7 assertion in application integration test | Pass |
| Media bytes stay outside PostgreSQL | Object-storage port, checksum/traversal lifecycle tests | Pass |
| Solr remains rebuildable | Projection handler, outbox/replay design, Solr integration tests | Pass |
| Localization enters search projection | Reviewed searchable text contract and projection tests | Pass |
| Module rules are automatic | ArchUnit visibility, graph, public-API, and cycle tests | Pass |
| Workspace authorization and capabilities | Identity/capability integration suites | Pass |
| Audit, AI, rights, and MTI seams | Focused integration/unit suites and architecture documents | Pass |
| CI verifies backend, frontend, and hygiene | `.github/workflows/build.yml` | Pass when WS14 CI is green |
| Foundation documents match implementation | Architecture register and executable-source links | Pass |

## Accepted residual risks

- Public HTTP/authentication contracts begin with backend/frontend integration.
- Production topology, backups, telemetry, security operations, and provider credentials
  need environment-specific implementation before release.
- Browser E2E, accessibility, performance, and security test suites begin with real user
  workflows.
- MTI governance, legal/IP conclusions, and FTRP remain separate approved workstreams.

These items are explicit later work, not hidden Sprint 0 failures.
