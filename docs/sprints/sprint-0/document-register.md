# Sprint 0 Document Register

Documents are produced when their subject becomes concrete enough to guide or verify implementation. They are updated in the same change as affected code.

| ID | Document | Type | Status | Purpose |
| --- | --- | --- | --- | --- |
| A-01 | System Context and Containers | Architecture | Planned | Users, external systems, deployable boundaries |
| A-02 | Module Architecture | Architecture | Planned | Platform/domain modules and allowed dependencies |
| A-03 | Data and Asset Architecture | Architecture | Planned | PostgreSQL ownership, migrations, storage boundaries |
| A-04 | Security and Tenancy Architecture | Architecture | Planned | Authentication seam, workspace isolation, authorization |
| A-05 | AI Provider Architecture | Architecture | Planned | AI Engine port, Grok adapter, provider-neutral contracts |
| A-06 | Logging and Observability Architecture | Architecture | Planned | Audit events, operational telemetry, correlation |
| A-07 | Deployment Architecture | Architecture | Planned | Environments, runtime services, configuration |
| D-01 | Repository and Package Design | Software design | Planned | Source layout, package ownership, dependency direction |
| D-02 | Workspace and Identity Design | Software design | Planned | Entities, services, authorization ports |
| D-03 | Capability Engine Design | Software design | Planned | Evaluation inputs, decisions, API |
| D-04 | Logging Engine Design | Software design | Planned | Event schema, append flow, retention seam |
| D-05 | AI Engine Design | Software design | Planned | Task contract, adapter SPI, normalized results |
| D-06 | API Conventions | Software design | Planned | Versioning, errors, pagination, idempotency |
| D-07 | Database Schema | Software design | Planned | Tables, keys, indexes, migration ownership |
| D-08 | Test Strategy | Software design | Planned | Unit, integration, architecture, contract, migration tests |

## ADR register

| ADR | Decision | Status |
| --- | --- | --- |
| ADR-0002 | Repository and application structure | Proposed |
| ADR-0003 | Spring Boot version, Java version, and build tool | Proposed |
| ADR-0004 | Modular architecture enforcement | Proposed |
| ADR-0005 | Persistence and migration strategy | Proposed |
| ADR-0006 | Identity and authentication boundary | Proposed |
| ADR-0007 | Audit-event model | Proposed |
| ADR-0008 | AI provider port and adapter contract | Proposed |

## Documentation rule

Architecture explains boundaries and qualities. Software design explains implementable structures and contracts. ADRs explain consequential choices. API definitions, schemas, and tests remain executable sources of truth where possible.
