# ADR-0016: Capability-driven product

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex supports multiple production modes, subscriptions, add-ons, roles, providers, and entitlements while following **No use. No see.**

## Decision

Capabilities determine function visibility, availability, and UI presentation. Capability evaluation supports subscriptions, production modes, AI enablement, entitlements, roles, workspace policy, and provider availability.

AI Studio is an add-on capability; users may use Filminex without mandatory AI or animation features. Pricing logic does not become embedded separately in every feature.

The Capability Engine's schema and internal API remain software-design decisions.

## Consequences

- Hidden capabilities are also unavailable, not merely visually concealed.
- UI and application authorization consume consistent decisions.
- Replacing capability-driven behavior requires a superseding ADR.
