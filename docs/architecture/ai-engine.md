# AI Engine

**Status:** Implemented foundation  
**Workstream:** Sprint 0 — 9/14  
**Decision:** [ADR-0009](decisions/0009-ai-provider-architecture.md)

## Boundary

Filminex modules submit provider-neutral tasks through `AiService`. Requests describe the
task, production mode, instruction, negative prompt, reference-asset keys, safe options,
request identity, and correlation identity. They do not contain Grok request objects or
provider SDK types.

Every call is evaluated through the Capability Engine before a provider is invoked.
Denied calls never reach the adapter. Provider availability participates in that decision,
so manual and real-production workflows remain operational when AI is disabled.

## Normalized results and provenance

Results retain:

- request identity;
- provider and model;
- normalized output and finish reason;
- safety metadata;
- normalized usage; and
- completion time.

Outputs are proposals for review. This foundation does not apply generated or inferred
values directly to authoritative Filminex domain state.

## Adapters

The provider SPI is visible only to the AI module. Architecture tests prevent filmmaking
modules from depending on it.

- `LocalAiProvider` is the deterministic default for development and CI. It performs no
  network call and marks its output as review-required.
- `GrokAiProvider` isolates Grok configuration and provider identity. Its network
  transport is intentionally unavailable in Sprint 0 until the API contract, credential
  delivery, timeout, retry, and safety policy are approved and tested.

Selecting `grok` without an enabled transport reports the provider unavailable; it never
silently falls back to another provider.

## Audit and secret boundary

Allowed, denied, and failed requests append durable WS7 audit events with request,
workspace, user, provider, model, task, outcome, and correlation provenance. Instructions,
negative prompts, outputs, reference content, and credentials are excluded from audit
details.

Configuration is environment-driven. `FILMINEX_GROK_API_KEY` has no repository default
and is never returned by provider status.

## Deferred

- live Grok HTTP transport and credential provisioning;
- provider retry, quota, streaming, and rate-limit policies;
- image or media byte transfer;
- prompt-template persistence;
- proposal review persistence and domain application;
- reverse-population schemas and confidence policies; and
- GPT, Gemini, Claude, and local-model adapters.
