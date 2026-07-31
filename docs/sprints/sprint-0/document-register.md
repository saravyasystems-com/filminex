# Sprint 0 Document Register

Documents are updated with the implementation they guide or verify. `Implemented foundation`
means the Sprint 0 boundary is recorded; later feature and production depth remains versioned work.

| ID | Document | Type | Status | Purpose |
| --- | --- | --- | --- | --- |
| A-01 | System Context and Containers | Architecture | Implemented foundation | Users, external systems, deployable boundaries |
| A-02 | Module Architecture | Architecture | Implemented foundation | Capability boundaries, ports, adapters, allowed dependencies |
| A-03 | Data and Asset Architecture | Architecture | Implemented foundation | PostgreSQL ownership, migrations, object-storage boundaries |
| A-04 | Search and Indexing Architecture | Architecture | Implemented foundation | Solr projection, indexing worker, replay and rebuild |
| A-05 | Security and Tenancy Architecture | Architecture | Implemented foundation | Authentication seam, workspace isolation, authorization |
| A-06 | AI Provider Architecture | Architecture | Implemented foundation | AI Engine port, Grok adapter, provider-neutral contracts |
| A-07 | Logging and Observability Architecture | Architecture | Implemented foundation | Audit events, operational telemetry boundary, correlation |
| A-08 | Rights Provider Architecture | Architecture | Implemented foundation | Local provider and deferred FTRP integration |
| A-09 | Localization Architecture | Architecture | Implemented foundation | Subtitle, dubbing, transcript, voice and export boundaries |
| A-10 | Media Transparency Architecture | Architecture | Implemented foundation | MTI evaluation and export disclosure boundary |
| A-11 | Deployment Architecture | Architecture | Implemented foundation | Environments, runtime services, configuration |
| A-12 | Frontend Architecture | Architecture | Implemented foundation | React/TypeScript/Vite workspace boundary and deferred API integration |
| D-01 | Repository and Package Design | Software design | Implemented foundation | Source layout, package ownership, dependency direction |
| D-02 | Workspace and Identity Design | Software design | Implemented | Entities, services, authorization ports |
| D-03 | Capability Engine Design | Software design | Implemented foundation | Evaluation inputs, decisions, API |
| D-04 | Logging and Audit Design | Software design | Implemented | Event schema, append flow, retention and redaction seams |
| D-05 | AI Engine Design | Software design | Implemented foundation | Task contract, adapter SPI, normalized results |
| D-06 | Search and Indexing Design | Software design | Implemented foundation | Projection schema, outbox consumer, retries and rebuild |
| D-07 | Rights Provider Design | Software design | Implemented foundation | Request/decision contract and local adapter |
| D-08 | Localization Design | Software design | Implemented foundation | Tracks, cues, dub lines, locale and voice-profile contracts |
| D-09 | MTI Integration Design | Software design | Implemented foundation | Applicability decision and image/video/audio disclosure output |
| D-10 | API Conventions | Software design | Implemented foundation | Versioning, errors, pagination, idempotency |
| D-11 | Database Schema | Software design | Implemented foundation | Tables, keys, indexes, migration ownership |
| D-12 | Test Strategy | Software design | Implemented foundation | Unit, integration, architecture, contract, migration tests |
| D-13 | Frontend Foundation Design | Software design | Implemented foundation | Application shell, context state, capability visibility, inspector, test and build conventions |

## ADR register

| ADR | Decision | Status |
| --- | --- | --- |
| ADR-0001 | Product vision | Frozen |
| ADR-0002 | Product principles | Frozen |
| ADR-0003 | Story hierarchy | Frozen |
| ADR-0004 | Modular monolith | Frozen |
| ADR-0005 | Persistence architecture | Frozen |
| ADR-0006 | Media storage | Frozen |
| ADR-0007 | Search architecture | Frozen |
| ADR-0008 | Event-driven indexing | Frozen |
| ADR-0009 | AI provider architecture | Frozen |
| ADR-0010 | Domain libraries | Frozen |
| ADR-0011 | Character identity versus scene presentation | Frozen |
| ADR-0012 | Property cascading with provenance | Frozen |
| ADR-0013 | Reverse data population | Frozen |
| ADR-0014 | Localization | Frozen |
| ADR-0015 | Talent rights boundary | Frozen |
| ADR-0016 | Capability-driven product | Frozen |
| ADR-0017 | Logging and provenance | Frozen |
| ADR-0018 | Media Transparency Indicator boundary | Frozen |
| ADR-0019 | Deepfake Icon applicability and behavior | Frozen |
| ADR-0020 | Governance boundary | Accepted |
| ADR-0021 | Intellectual-property workstream | Accepted |
| ADR-0022 | Sprint 0 scope | Frozen |
| ADR-0023 | ADR governance | Frozen |

## Governance workstream

The Media Transparency Foundation requires an MTI vision/specification, Deepfake Icon guidance, a Media Transparency Council charter draft, an IP strategy, and an FTRP-boundary document. These governance documents complement ADR-0018 through ADR-0021; unresolved governance and legal details remain Accepted or Draft rather than Frozen.

## Documentation rule

Architecture explains boundaries and qualities. Software design explains implementable structures and contracts. ADRs explain consequential choices. API definitions, schemas, migrations, and tests remain executable sources of truth where possible.
