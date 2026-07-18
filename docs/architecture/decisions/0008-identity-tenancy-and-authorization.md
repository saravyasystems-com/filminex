# ADR-0008: Identity, tenancy, and authorization boundary

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex supports owned workspaces, collaboration, read/write modes, shared copies, capability entitlements, and future provider changes. Authentication technology must not define domain authorization.

## Decision

- Treat Workspace as the primary tenancy and authorization boundary.
- Use standards-based OIDC/OAuth 2.1 authentication through an identity-provider adapter.
- Maintain application-owned users, memberships, roles, invitations, and entitlement references in PostgreSQL.
- Authorization decisions combine authenticated subject, workspace membership, resource relationship, requested action, and capability decision.
- Enforce authorization in application use cases and query projections, including Solr filters and asset transfers.
- Keep identity-provider claims outside core domain models.
- Billing providers may change entitlements only through Capability Engine contracts; billing is not an authorization shortcut.

## Consequences

- Authentication providers can change without rewriting workspace rules.
- Every workspace-owned record and index projection requires a tenant identifier.
- Background jobs carry explicit workspace and actor/system context.
- Fine-grained policy growth remains possible behind an authorization port.

## Verification

- Cross-workspace reads and writes are denied by tests.
- Read-mode collaborators cannot perform write operations.
- Service/background actions are auditable with an explicit principal.
