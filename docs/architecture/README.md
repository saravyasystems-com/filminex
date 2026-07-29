# Filminex Architecture

## Baseline

[Genesis Chapter 5](../genesis/05-Platform-Architecture.md) declares the layered architecture and platform engines. Sprint architecture documents translate that declaration into implementable and verifiable boundaries.

## Documentation model

- **Genesis:** frozen product and platform baseline
- **Architecture:** current system structure, boundaries, and quality attributes
- **Software design:** module internals, interfaces, schemas, and contracts

## Implemented designs

- [Modular monolith dependency design](module-dependencies.md)
- [PostgreSQL and migrations](postgresql-and-migrations.md)
- [Object storage](object-storage.md)
- [Solr infrastructure](solr-infrastructure.md)
- [Event and outbox foundation](event-outbox.md)
- [Workspace and identity foundation](workspace-and-identity.md)
- [Logging and audit foundation](logging-and-audit.md)
- [Capabilities and entitlements foundation](capabilities-and-entitlements.md)
- **ADRs:** why consequential technical choices were made
- **Sprint artifacts:** delivery scope, sequencing, evidence, and exit review
- **Code, API definitions, migrations, and tests:** executable truth

## Current delivery

See [Project State](../PROJECT_STATE.md), [Sprint 0](../sprints/sprint-0/README.md), its [document register](../sprints/sprint-0/document-register.md), and the [ADR index](decisions/README.md).

Later accepted decisions do not rewrite frozen Genesis. They are recorded through current architecture, software design, ADRs, governance specifications, executable schemas, and tests.
