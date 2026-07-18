# ADR-0012: Localization as a first-class capability

- **Status:** Accepted
- **Date:** 2026-07-19

## Context

Subtitles and dubbing participate in dialogue, scenes, timing, talent/characters, voice identity, rights, media assets, search, and export. Modeling them as generic attachments would lose production meaning and consistency.

## Decision

- Establish Localization as a first-class capability boundary.
- Model locale/language, subtitle tracks and cues, transcript tracks/segments, dub tracks and lines, timing, speaker/character references, voice-profile references, source/target relationships, status, provenance, and linked assets.
- Localization references authoritative dialogue and scene timing through public contracts rather than direct persistence access.
- Dubbing that uses a governed human voice invokes the RightsProvider; AI/hybrid synthetic or materially altered human voice also invokes MTI evaluation.
- Index authorized subtitle, transcript, and dub text and facets in Solr.
- Export/render pipelines consume versioned localization outputs through explicit contracts.

## Consequences

- Localization can be scheduled, reviewed, searched, versioned, and exported independently.
- Timing and dialogue changes require dependency detection and stale-state handling.
- Voice profiles remain separate from audio binaries and provider-specific voice IDs.
- Localization UI is deferred, but its contracts begin in the platform/domain design.

## Verification

- Locale and timing invariants are tested.
- Dialogue changes mark affected localization work for review.
- Search and rights tests cover localization content.
