# ADR-0011: Rights provider and future FTRP boundary

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex needs production-facing participation and ethical synthetic-likeness/voice checks. The future Film Talent Rights Platform (FTRP) is a separate Saravya project for media-creation consent, access/governance, audit, and provenance. Filminex must release first and must not absorb FTRP internals.

## Decision

- Define a provider-neutral `RightsProvider` port for rights inquiries, requests, decisions, evidence references, validity, and objections/exceptions.
- Implement a local Filminex provider first using application-owned records.
- Allow a future FTRP adapter without changing filmmaking-domain use cases.
- Physical-production participation may default to approved subject to recorded objections/exceptions and project policy.
- AI/hybrid generation involving governed human likeness or voice requires an explicit reviewable rights decision.
- Filminex stores provider references and decision snapshots required to explain an action; the external provider remains authoritative when configured.
- Talent availability/date requests may be presented in Filminex, but authoritative rights governance remains behind the provider boundary.

## Consequences

- Filminex can ship before FTRP while preserving an integration seam.
- The local provider is intentionally not a hidden implementation of the full FTRP platform.
- Provider outages require explicit fail-closed/fail-review policy per operation.
- Rights decisions and MTI disclosures are related but separate concerns.

## Deferred

- FTRP organization, platform, cross-project governance, external audit, and provenance network.
- Final ownership of advanced consent scheduling beyond Filminex's production-facing request flow.
