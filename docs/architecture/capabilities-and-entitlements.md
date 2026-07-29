# Capabilities and Entitlements

**Status:** Implemented foundation  
**Workstream:** Sprint 0 — 8/14  
**Decision:** [ADR-0016](decisions/0016-capability-driven-product.md)

## Boundary

The capability engine gives application and user-interface callers one explainable answer
to the same question: may this user use this function in this workspace and context?
Hiding a control is never treated as authorization.

Evaluation combines:

- workspace membership and role;
- provider-neutral workspace entitlement;
- workspace policy;
- production mode; and
- provider availability.

The engine does not authenticate users, calculate prices, charge customers, or integrate
with a billing provider. Those delivery decisions can supply entitlements later without
changing feature modules.

## Initial capability vocabulary

Core capabilities are derived directly from WS6 roles:

| Capability | Viewer | Editor | Admin |
|---|---:|---:|---:|
| `WORKSPACE_READ` | Yes | Yes | Yes |
| `WORKSPACE_WRITE` | No | Yes | Yes |
| `WORKSPACE_ADMIN` | No | No | Yes |

Optional `AI_STUDIO` and `ANIMATION_STUDIO` capabilities require both Editor-or-Admin
access and an enabled workspace entitlement. AI Studio is relevant only to AI or hybrid
production and also requires an available provider. Animation Studio is relevant only to
animation or hybrid production.

## Persistence and administration

`workspace_entitlement` is authoritative PostgreSQL state keyed by workspace and
capability. It records an explicit enabled/disabled state, provider-neutral source,
administrator, and change time. Sources are `SUBSCRIPTION`, `ADD_ON`, and
`WORKSPACE_POLICY`; none embeds a commercial vendor or price.

Only workspace administrators can change entitlements. Any member can read the current
workspace entitlement set. Every successful change appends a WS7 business audit event
with the actor, capability, state, and source.

## Explainable enforcement

`CapabilityService.evaluate` returns an allow/deny decision plus a stable reason:

- not a workspace member;
- insufficient role;
- entitlement required;
- disabled by workspace policy;
- incompatible production mode; or
- provider unavailable.

Callers use that decision both to suppress irrelevant UI and to reject unavailable API
operations. This is the executable foundation for **No use. No see.**

## Deferred

- product plan and price catalogs;
- billing-provider integration;
- trials, quotas, usage metering, and invoicing;
- project/entity-specific policies beyond production mode;
- a complete feature-by-feature capability catalog; and
- frontend capability delivery.

Those additions extend the provider-neutral seam rather than distributing pricing logic
across filmmaking modules.
