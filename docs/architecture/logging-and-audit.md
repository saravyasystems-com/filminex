# Logging and Audit Foundation

Workstream 7 establishes a durable Logging Engine boundary for meaningful product history.
It implements ADR-0017 without confusing audit evidence with diagnostic application logs.

## Contract

Every audit event belongs to one workspace and records:

- a generated event identifier;
- a `USER`, `AI`, or `SYSTEM` actor and optional system actor identifier;
- a stable action name;
- a subject type and identifier;
- a `SUCCEEDED`, `DENIED`, or `FAILED` outcome;
- occurrence time;
- correlation and optional causation identifiers;
- small structured details that explain the transition.

Callers append through `AuditService`. They can retrieve an event by workspace and identifier
or query recent workspace history using actor, action, subject, outcome, correlation, and time
filters. Results are newest first and bounded to 500 records.

## Evidence integrity

PostgreSQL is authoritative. The `audit_event` table is append-only: a database trigger rejects
updates and deletes, including changes attempted outside the Java adapter. A correction is a new
event that correlates or refers to the earlier evidence; it never rewrites the earlier row.

Workspace identity is mandatory in every write and read, preventing accidental cross-workspace
queries. Indexes support workspace timeline, subject history, and correlation inspection.

## Data minimization

Audit details are explanatory metadata, not a storage location for request bodies, media, prompts,
credentials, or personal data. The adapter rejects detail keys associated with passwords, secrets,
tokens, credentials, authorization headers, and cookies before persistence.

Future domain integrations should use stable identifiers and concise before/after summaries.
Retention policy and privileged audit-reader authorization remain policy work and must not weaken
append-only evidence.

## Separate concerns

- Audit history records meaningful product and decision events durably.
- The WS5 outbox transports domain events asynchronously and may be retried.
- Operational logs and metrics diagnose runtime behavior and follow different retention rules.
- Analytics may consume audit events later but cannot redefine their original meaning.
- WS8 evaluates capabilities and entitlements; it is not implemented here.
