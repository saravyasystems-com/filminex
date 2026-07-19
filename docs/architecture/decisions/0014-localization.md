# ADR-0014: Localization

- **Status:** Frozen
- **Date:** 2026-07-19

## Context

Subtitles, captions, transcripts, dubbing, and language versions participate in dialogue, timing, voice, rights, media, search, and export. They are not generic attachments.

## Decision

Localization is a first-class module. It includes subtitles, captions, transcripts, dubbing, and language versions. Search indexes authorized localization data.

Sprint 0 establishes the module seam, language/locale concepts, stable dialogue/timing references, voice-profile abstraction, and searchable subtitle/transcript fields. Final localization schemas and full studio workflows remain future work.

## Consequences

- Localization can be versioned, reviewed, searched, and exported coherently.
- Human, AI, and hybrid dubbing can share contracts without sharing implementations.
- Treating localization as simple attachment metadata requires a superseding ADR.
