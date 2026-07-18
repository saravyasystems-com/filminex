# Sprint 0 Document Register

Documents are updated with the implementation they guide or verify. `Planned` means required for Sprint 0 exit; `In progress` means a governing ADR or initial implementation exists.

| ID | Document | Type | Status | Purpose |
| --- | --- | --- | --- | --- |
| A-01 | System Context and Containers | Architecture | Planned | Users, external systems, deployable boundaries |
| A-02 | Module Architecture | Architecture | In progress | Capability boundaries, ports, adapters, allowed dependencies |
| A-03 | Data and Asset Architecture | Architecture | In progress | PostgreSQL ownership, migrations, object-storage boundaries |
| A-04 | Search and Indexing Architecture | Architecture | In progress | Solr projection, indexing worker, replay and rebuild |
| A-05 | Security and Tenancy Architecture | Architecture | In progress | Authentication seam, workspace isolation, authorization |
| A-06 | AI Provider Architecture | Architecture | In progress | AI Engine port, Grok adapter, provider-neutral contracts |
| A-07 | Logging and Observability Architecture | Architecture | In progress | Audit events, operational telemetry, correlation |
| A-08 | Rights Provider Architecture | Architecture | In progress | Local provider and deferred FTRP integration |
| A-09 | Localization Architecture | Architecture | In progress | Subtitle, dubbing, transcript, voice and export boundaries |
| A-10 | Media Transparency Architecture | Architecture | In progress | MTI evaluation and export disclosure boundary |
| A-11 | Deployment Architecture | Architecture | Planned | Environments, runtime services, configuration |
| D-01 | Repository and Package Design | Software design | In progress | Source layout, package ownership, dependency direction |
| D-02 | Workspace and Identity Design | Software design | Planned | Entities, services, authorization ports |
| D-03 | Capability Engine Design | Software design | Planned | Evaluation inputs, decisions, API |
| D-04 | Logging and Audit Design | Software design | Planned | Event schema, append flow, retention and redaction seams |
| D-05 | AI Engine Design | Software design | Planned | Task contract, adapter SPI, normalized results |
| D-06 | Search and Indexing Design | Software design | Planned | Projection schema, outbox consumer, retries and rebuild |
| D-07 | Rights Provider Design | Software design | Planned | Request/decision contract and local adapter |
| D-08 | Localization Design | Software design | Planned | Tracks, cues, dub lines, locale and voice-profile contracts |
| D-09 | MTI Integration Design | Software design | Planned | Applicability decision and image/video/audio disclosure output |
| D-10 | API Conventions | Software design | Planned | Versioning, errors, pagination, idempotency |
| D-11 | Database Schema | Software design | Planned | Tables, keys, indexes, migration ownership |
| D-12 | Test Strategy | Software design | Planned | Unit, integration, architecture, contract, migration tests |

## ADR register

| ADR | Decision | Status |
| --- | --- | --- |
| ADR-0001 | Documentation-first repository foundation | Accepted |
| ADR-0002 | JVM application stack and build system | Accepted |
| ADR-0003 | Repository structure and modular-monolith layout | Accepted |
| ADR-0004 | Modular architecture enforcement | Accepted |
| ADR-0005 | PostgreSQL persistence and migration strategy | Accepted |
| ADR-0006 | Media asset storage and versioning | Accepted |
| ADR-0007 | Solr search and event-driven indexing | Accepted |
| ADR-0008 | Identity, tenancy, and authorization boundary | Accepted |
| ADR-0009 | Audit event and provenance model | Accepted |
| ADR-0010 | AI provider port and adapter contract | Accepted |
| ADR-0011 | Rights provider and future FTRP boundary | Accepted |
| ADR-0012 | Localization as a first-class capability | Accepted |
| ADR-0013 | Media Transparency Indicator integration boundary | Accepted |

## Governance workstream

The Media Transparency Foundation also requires MTI vision/specification, Deepfake Icon guidance, an MTCouncil charter draft, and an IP strategy. These governance documents complement ADR-0013; they do not redefine application architecture.

## Documentation rule

Architecture explains boundaries and qualities. Software design explains implementable structures and contracts. ADRs explain consequential choices. API definitions, schemas, migrations, and tests remain executable sources of truth where possible.
