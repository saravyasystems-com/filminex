# Localization Foundation

**Status:** Implemented foundation
**Workstream:** Sprint 0 — 11/14
**Decision:** [ADR-0014](decisions/0014-localization.md)

## Boundary

Localization is authoritative domain data rather than attachment metadata. A track belongs
to one workspace and one stable source reference. Its kind is subtitle, caption,
transcript, or dub, and its locale is represented by a normalized language tag.

Timed cues preserve sequence, millisecond timing, text, an optional stable dialogue
reference, and an optional voice profile. Track status separates draft content from
reviewed or approved content.

## Voice profiles

A voice profile references a stable talent identifier, locale, label, and origin:
`HUMAN`, `AI`, or `HYBRID`. It does not contain provider credentials, synthesized audio,
consent evidence, or a vendor-specific voice identifier. Those integrations must use the
asset, rights, and AI boundaries in later work.

## Search projection

`LocalizationService.searchableEntries` exposes provider-neutral reviewed or approved
text. The search module may transform these entries into its rebuildable Solr projection.
Draft text is excluded. PostgreSQL remains authoritative.

## Isolation and integrity

Every query includes the workspace boundary. Tracks and voice profiles from another
workspace cannot be attached to a cue. Database constraints protect track identity, cue
order, timing, and controlled vocabularies.

## Deferred

- translation and speech providers;
- waveform, lip-sync, and studio editing;
- automated rights evaluation for synthesized voices;
- asset-version association and rendered subtitle files;
- full review workflow and collaborator permissions; and
- production UI and export formats.
