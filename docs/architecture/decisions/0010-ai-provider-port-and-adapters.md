# ADR-0010: AI provider port and adapter contract

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Filminex initially uses Grok but must support GPT, Gemini, Claude, local models, and non-AI operation. Reverse population and generation must remain reviewable and provider independent.

## Decision

- All AI work passes through an AI Engine application port expressed in Filminex task vocabulary.
- Provider adapters translate normalized requests and results; Grok is the initial configured adapter.
- Provider names, models, credentials, quotas, and safety settings are configuration—not domain types.
- Persist prompt specifications, rendered requests where policy allows, normalized results, provider/model metadata, cost/usage metadata, and correlation IDs.
- AI output is a proposal until accepted. Inferred fields retain confidence and provenance.
- Capability Engine authorizes AI use independently of UI visibility.
- Timeouts, retry eligibility, idempotency, cancellation, and provider errors are normalized.
- No provider SDK crosses the adapter boundary.

## Consequences

- Provider changes do not rewrite filmmaking use cases.
- Normalization may not expose every provider-specific feature; extensions require explicit contracts.
- Filminex remains usable with AI disabled.
- Rights and MTI evaluation occur through separate ports and cannot be bypassed by an adapter.

## Verification

- Contract tests run against every provider adapter.
- Core modules compile without provider SDK dependencies.
- AI-disabled tests cover the same non-AI workflows.
