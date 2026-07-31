# API Conventions

**Status:** Foundation contract

Sprint 0 establishes Java module APIs, not a public HTTP product API. Until an HTTP
contract is versioned, adapters must preserve these rules:

- identify actor, workspace, correlation, and causation explicitly;
- use UUIDs for durable identities and UTC instants for time;
- validate requests at the boundary and return stable reason enums for expected denials;
- do not expose provider-specific payloads across provider-neutral ports;
- scope reads and pagination inside the workspace;
- make retried mutations idempotent before exposing them remotely;
- map unexpected failures to non-sensitive errors while retaining correlation identity.

Authentication headers, URL versioning, JSON error envelopes, pagination tokens, and
idempotency headers will be finalized with the first backend/frontend integration.
