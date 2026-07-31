# System Context and Containers

**Status:** Implemented Sprint 0 foundation

Filminex is a filmmaking workspace used by workspace members in viewer, editor, or
administrator roles. External AI, identity, rights, and object-storage products are
replaceable providers; Sprint 0 uses local adapters and never makes one provider the
domain authority.

## Runtime containers

| Container | Responsibility | Authority |
|---|---|---|
| `filminex-web` | React browser shell and capability-filtered presentation | No business authority |
| `filminex-api` | Spring Boot modular monolith and business policies | Application behavior |
| PostgreSQL | Workspaces, audit, entitlements, rights, localization, and outbox | Authoritative records |
| Object storage | Versioned media bytes and checksums | Authoritative media bytes |
| Solr | Workspace-scoped media/localization search | Rebuildable projection |

The browser will call versioned HTTP APIs once those contracts are introduced. The API
alone enforces authorization and capabilities. Outbox events carry committed changes to
derived consumers such as Solr. Provider credentials remain server-side configuration.

## Trust boundaries

- Every request and query is scoped to a workspace.
- Browser visibility is usability, not authorization.
- PostgreSQL and object storage are authoritative; Solr can be discarded and rebuilt.
- AI, rights, and future identity integrations enter through provider-neutral ports.
- Audit evidence is distinct from operational logs and rejects sensitive detail keys.
