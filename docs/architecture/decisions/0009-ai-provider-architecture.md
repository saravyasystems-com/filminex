# ADR-0009: AI provider architecture

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Filminex initially uses Grok but must not make filmmaking capabilities dependent on a single provider or on AI availability.

## Decision

AI providers are accessed through a provider-neutral application boundary. **Grok** is the initial provider. GPT, Gemini, and other providers may be added later.

Business modules never depend on provider implementations or SDKs. Provider, model, credentials, and quotas are configuration. AI output remains reviewable and retains provider and provenance information.

## Consequences

- Filminex continues functioning without AI.
- Providers can be replaced or added behind adapters.
- Provider-specific functionality requires an explicit extension rather than leaking into the domain.
- Changing the provider-neutral architecture requires a superseding ADR.
