# Workspace and Identity Foundation

**Status:** Implemented baseline
**Workstream:** Sprint 0 — 6/14

## Boundary

A workspace is Filminex's ownership, collaboration, and tenancy boundary. Users belong to
workspaces through memberships. A user can have a different role in every workspace.

Authentication remains provider neutral and is deliberately outside this workstream. The
identity module owns Filminex user records and workspace membership; it does not select an
external login provider or store provider-specific credentials.

## Authoritative model

PostgreSQL owns three records:

| Record | Purpose |
|---|---|
| `filminex_user` | Provider-neutral Filminex user identity |
| `workspace` | Ownership and tenancy boundary |
| `workspace_membership` | User role in one workspace |

Email uniqueness is case-insensitive. A membership is unique by workspace and user.
Foreign keys prevent memberships from referring to missing users or workspaces.

## Roles

| Role | Foundation meaning |
|---|---|
| `VIEWER` | Read-mode workspace participation |
| `EDITOR` | Write-mode workspace participation |
| `ADMIN` | Membership and workspace administration |

These roles are intentionally coarse. Workstream 8 will evaluate fine-grained capabilities
and entitlements. UI visibility never substitutes for server-side authorization.

## Invariants

- Creating a workspace makes its creator an `ADMIN` in the same transaction.
- Only a workspace `ADMIN` can add, remove, or change members.
- Every workspace must retain at least one `ADMIN`.
- Membership administration serializes on the workspace row to protect the last-admin rule.
- Membership lookup always requires both workspace and user identifiers.
- Identity lifecycle changes publish durable Workstream 5 outbox events transactionally.

## Public API

Other modules use `identity.api.IdentityService` and its provider-neutral records. JDBC,
tables, event construction, and transaction management remain inside `identity.internal`.

The API supports:

- registering a Filminex user;
- creating a workspace;
- adding, removing, and changing member roles;
- resolving one membership;
- listing a workspace directory for an existing member.

HTTP endpoints, invitations, authentication adapters, account recovery, audit projections,
and fine-grained capability rules are later delivery slices.
