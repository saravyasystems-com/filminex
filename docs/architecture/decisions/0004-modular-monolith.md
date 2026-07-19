# ADR-0004: Modular monolith

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex needs strong domain boundaries without the deployment and operational cost of premature distributed services.

## Decision

Filminex adopts a **Modular Monolith** architecture.

Rules:

- Explicit module boundaries
- Stable public interfaces
- Internal implementation encapsulation
- Domain events where appropriate
- No direct access to another module's persistence internals
- No premature microservices

Exact package names, module grouping, and interface signatures remain software-design decisions until separately approved.

## Consequences

- Boundary rules must be enforceable through tests and review.
- Modules initially deploy together.
- A future service extraction requires evidence and a superseding or additional ADR.
