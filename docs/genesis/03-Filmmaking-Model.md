# Chapter 3 — Filmmaking Model

## Narrative and production hierarchy

```text
Workspace
└── Project
    └── Story
        └── Episode
            └── Scene
                └── Shot
                    └── Frame
                        └── Take
```

A film may use one implicit episode. Series use explicit episodes. Read mode can present the complete story without forcing users to navigate the editing hierarchy.

## Scene as production boundary

Each scene declares its production mode:

- real;
- AI;
- hybrid;
- animation.

The mode controls relevant tools and outputs but does not change the common story hierarchy.

## Global libraries

A workspace can maintain reusable libraries for:

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

Libraries provide reusable identity and production knowledge. A scene selects from them and supplies contextual assignments and overrides.

## Character versus scene

Character identity includes face, physique, hair, eyes, complexion, age, height, voice, distinguishing marks, reference images, and mannerisms.

Moles, tattoos, scars, and other identity marks belong to the character. Mannerisms are available as director knowledge.

Wardrobe, ornaments, placement, action, expression, and other moment-specific conditions belong to the scene or a lower production unit. Character identity must not be duplicated into every scene.

## Locations

A location can retain descriptive properties, a gist, and directional references. The target reference set is front, back, left, right, top, and bottom so spatial continuity can be maintained.

## Scene content

A scene may own or select:

- characters;
- mandatory wardrobe for each selected character;
- location;
- time and weather;
- lighting;
- camera and frame rate;
- dialogue and narration;
- props, expressions, motions, music, and VFX;
- global and scene-level director notes.

“Take 1” through the last take records alternative attempts without destroying the planned shot or frame.
