# Domain Model

## Production hierarchy

`Workspace → Story → Episode → Scene → Shot → Frame / Take`

- **Workspace:** tenancy, members, entitlements, shared libraries, and projects.
- **Story:** narrative root, global intent, and defaults.
- **Episode:** optional grouping for series; films may use one implicit episode.
- **Scene:** narrative and production unit with its own mode, cast, location, time, wardrobe, and direction.
- **Shot:** camera and action unit within a scene.
- **Frame / Take:** planned visual keyframe or recorded/generated attempt.

## Reusable libraries

- Characters
- Locations
- Wardrobe
- Camera
- Lighting
- Props
- Expressions
- Motions
- VFX
- Music

## Character identity

A character may include face, physique, hair, eyes, complexion, age, height, voice, distinguishing marks, reference images, and director-only mannerisms. Identity properties remain separate from per-scene wardrobe and action.

## Location identity

Locations hold descriptive properties and directional references. The target reference set supports front, back, left, right, top, and bottom views for spatial consistency.

## Scene context

A scene selects characters and assigns required wardrobe per selected character. It also owns location, time, weather, lighting, dialogue, narration, director notes, production mode, and scene-specific overrides.

## Property provenance

Every structured property should be representable as:

```text
value
source: manual | inferred | inherited | imported
sourceEntity
confidence (when inferred)
locked
updatedBy
updatedAt
```

Inheritance should be resolved predictably from broader to narrower scope. Overrides never erase their origin.

## Reverse population

- Script input can propose stories, episodes, scenes, shots, and frames.
- Character references can propose identity properties and directional views.
- Location references can propose descriptive properties, time/weather cues, and directional views.
- Scene references can propose scene gist and contextual properties.

All proposed values require review and retain inference metadata.

## Cross-cutting entities

- Membership and role
- Entitlement
- Comment and review
- Asset and asset version
- Prompt specification and rendered prompt
- Audit event
- Integration account
