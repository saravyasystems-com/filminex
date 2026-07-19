# Filminex

**The Filmmaking Workspace**

Filminex empowers talents to transform storylines into cinematic productions across real, AI, hybrid, and animation workflows.

## Product principles

- **No use. No see.** Keep unused capabilities out of the user's way.
- Make complex production knowledge explainable.
- Prefer structured, property-based creative control over opaque prompts.
- Preserve provenance: users should know where inherited values came from.
- Keep AI optional and provider-independent.
- Treat character identity, locations, wardrobe, and production intent as reusable knowledge.

## Production model

```text
Workspace
└── Story
    └── Episode
        └── Scene
            └── Shot
                └── Frame / Take
```

A scene may use real, AI, hybrid, or animation production mode. Global libraries supply reusable characters, locations, wardrobe, camera, lighting, props, expressions, motions, VFX, and music. Scene-level choices override inherited defaults with visible provenance.

## Repository status

Genesis and the resolved Sprint 0 architectural baseline are **frozen**. Sprint 0 — Platform Foundation is **in progress**.

The first API scaffold is present. Sprint 0 now implements and verifies the frozen boundaries for persistence, assets, search, event-driven indexing, AI, rights, localization, capabilities, provenance, and media transparency. Governance and intellectual-property strategy remain Accepted rather than Frozen.

## Start here

- [Genesis — frozen product and platform baseline](docs/genesis/README.md)
- [Product charter](docs/product/charter.md)
- [Product principles](docs/product/principles.md)
- [Domain model](docs/architecture/domain-model.md)
- [Architecture decisions](docs/architecture/decisions/README.md)
- [Current project state](docs/PROJECT_STATE.md)
- [Sprint 0 plan](docs/sprints/sprint-0/README.md)
- [Sprint 0 document register](docs/sprints/sprint-0/document-register.md)
- [Roadmap](docs/roadmap.md)
- [Contributing](CONTRIBUTING.md)

## Proposed top-level structure

```text
docs/           Product, architecture, and delivery knowledge
apps/           Deployable applications (introduced after stack ADR)
packages/       Shared modules (introduced after stack ADR)
infra/          Deployment and infrastructure definitions
scripts/        Repeatable development and maintenance utilities
```

Empty implementation directories are documented here and will be added with their first real files; Git does not track empty directories.

## Foundation data flow

- **PostgreSQL** is the authoritative system of record.
- **Object storage** holds versioned media binaries.
- **Apache Solr** is a rebuildable search index for media and localization content.
- Provider-specific AI, search, storage, identity, and rights integrations remain behind platform ports.

## Build

Java 25 is required. Use the checked-in Gradle wrapper:

```shell
./gradlew test
./gradlew :apps:filminex-api:bootRun
```

The Sprint 0 frontend direction is React, TypeScript, and Vite; its scaffold is the next engineering foundation deliverable.

## License

Copyright © Saravya Systems. All rights reserved. No open-source license has been granted yet.
