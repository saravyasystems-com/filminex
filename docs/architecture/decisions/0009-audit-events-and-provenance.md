# ADR-0009: Audit event and provenance model

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex must explain manual, inferred, inherited, imported, AI-generated, and system-produced changes. Diagnostic logs alone are mutable, retention-oriented operational data and cannot serve as durable audit evidence.

## Decision

- Record append-only audit events in PostgreSQL for consequential business actions and decisions.
- Each event includes event ID, occurred/recorded time, workspace, actor type and ID, action, subject type and ID, correlation/causation IDs, source, and safe structured details.
- Structured property values retain provenance: source kind, source entity, confidence when inferred, lock state, updater, and update time.
- Audit events never contain secrets, raw provider credentials, or unnecessary sensitive media.
- Operational logs and traces use the same correlation IDs but remain separate from the audit ledger.
- Corrections append compensating events; they do not rewrite history.
- Retention, redaction, export, and legal-hold behavior are explicit policy seams.

## Consequences

- User-visible history and system accountability can share a durable foundation.
- Storage grows append-only and needs partition/retention planning.
- Not every debug statement becomes an audit event.
- AI, rights, MTI, indexing, and asset lifecycle actions gain consistent evidence.

## Verification

- Consequential use cases assert expected audit events.
- Events remain ordered per aggregate where ordering matters.
- Sensitive-field tests prevent prohibited payloads.
