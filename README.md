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

The repository is in **Genesis / Sprint 0**. The current foundation is intentionally documentation-first so product decisions can stabilize before a technical stack is selected.

## Start here

- [Product charter](docs/product/charter.md)
- [Product principles](docs/product/principles.md)
- [Domain model](docs/architecture/domain-model.md)
- [Architecture decisions](docs/architecture/decisions/README.md)
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

## License

Copyright © Saravya Systems. All rights reserved. No open-source license has been granted yet.
