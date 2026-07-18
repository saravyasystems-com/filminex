# Chapter 5 — Platform Architecture

## Purpose

This chapter defines the platform architecture that powers Filminex and future Saravya Systems products. It separates reusable platform capabilities from filmmaking-specific functionality.

## Architectural goals

1. Keep domain logic independent of infrastructure.
2. Make AI an optional capability.
3. Build reusable platform services for future products.
4. Support explainable knowledge rather than isolated records.
5. Minimize coupling between crafts.

## Layered architecture

```text
+------------------------------------------------------+
| User Experience                                      |
| Workspaces • Perspectives • Context Inspector        |
+------------------------------------------------------+
| Filminex Domain                                      |
| Story • Characters • Scenes • Camera • Music • VFX  |
+------------------------------------------------------+
| Saravya Platform                                     |
| Knowledge • Resolution • AI • Logging • Capability   |
| Search • Sharing • Identity                          |
+------------------------------------------------------+
| Infrastructure                                       |
| Spring Boot • PostgreSQL • Storage • Cache • Queue   |
+------------------------------------------------------+
```

## Platform engines

### Knowledge Engine

Maintains relationships between entities, performs dependency discovery, and enables navigation and search.

### Resolution Engine

Calculates the effective value of every property while preserving provenance and explainability.

### AI Engine

Provides a provider-agnostic abstraction. The initial provider is **Grok**. Future GPT, Gemini, Claude, or local-model adapters plug into the same interface.

No Filminex module may call Grok directly.

### Capability Engine

Controls feature availability based on licensing, production mode, and add-ons. AI Studio is an add-on capability, not a separate product.

### Logging Engine

Captures user actions, AI actions, system events, and provenance for auditability and future analytics.

### Identity and Sharing

Supports ownership, collaboration, read mode, write mode, and promotion of shared copies.

## Domain layer

The domain layer contains filmmaking concepts: Story, Episode, Scene, Shot, Frame, Character, Location, Wardrobe, Camera, Dialogue, Music, VFX, and Marketing. Business rules belong here, not in the platform layer.

## User experience layer

The experience is built around workspaces, perspectives, and a context inspector. Context is the interface. “No use. No see.” applies. Explainability precedes automation.

## AI architecture

```text
Workspace
   |
AI Engine
   |
Provider Adapter
   |
Grok / GPT / Gemini / Claude / Local
```

If AI is disabled, the remainder of the application continues to function.

## Reuse

The platform is intentionally reusable:

- Filminex → filmmaking knowledge
- CVShot → career knowledge
- TradingRobo → trading knowledge

Only domain vocabulary and rules change.

## Declared design decisions

- Platform first, product second.
- Knowledge over CRUD.
- Explainability over hidden automation.
- Optional AI.
- Feature availability through the Capability Engine.
