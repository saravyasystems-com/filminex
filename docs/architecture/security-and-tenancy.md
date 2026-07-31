# Security, Tenancy, and Authorization

**Status:** Implemented Sprint 0 foundation

The workspace is Filminex's tenant and ownership boundary. A provider-neutral user gains
access only through a workspace membership with `VIEWER`, `EDITOR`, or `ADMIN` role.

## Enforcement rules

- Services resolve membership inside the requested workspace before protected work.
- Administrative mutations require `ADMIN`; the final administrator cannot be removed.
- Capability decisions combine role, entitlement, production mode, workspace policy,
  and provider availability. UI visibility cannot grant access.
- Queries and persistence operations include workspace identity; cross-workspace
  references are rejected and covered by integration tests.
- Sensitive values are supplied by environment/configuration boundaries and prohibited
  from structured audit details.

Authentication provider selection, session/token transport, public API threat modeling,
rate limiting, encryption-key operations, and production incident response are deferred.
They must preserve the established workspace/membership authorization seam.
