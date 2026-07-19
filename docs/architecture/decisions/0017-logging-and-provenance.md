# ADR-0017: Logging and provenance

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Technical server logs alone cannot explain creative changes, inheritance, AI involvement, rights state, or approval history.

## Decision

Filminex logging preserves meaningful product history, including changes, provenance, inheritance source, AI generation source, rights status, and approval history.

Operational telemetry and durable product/audit history are separate concerns but share correlation identifiers. Corrections append history rather than silently rewriting evidence. Exact schemas, retention, redaction, and Logging Engine internals remain software-design and policy work.

## Consequences

- Users and maintainers can explain how consequential state was produced.
- AI, inheritance, rights, and approvals require consistent evidence.
- Treating logging as technical diagnostics only requires a superseding ADR.
