# Chapter 6 — Production Knowledge Model

## Entity knowledge

Filminex stores entities, properties, relationships, assets, versions, and provenance. The useful unit is not an isolated record; it is an explainable production fact in context.

## Property record

A structured property supports:

```text
definition
declared value
effective value
source type
source entity
confidence
lock state
author
timestamps
version
```

Source types include manual, inferred, inherited, imported, and overridden.

## Resolution order

Broader scopes may provide defaults. Narrower scopes may override them:

```text
Workspace → Project → Story → Episode → Scene → Shot → Frame → Take
```

Not every property participates at every scope. Its definition declares valid owners and inheritance behavior. An override does not erase the upstream declaration.

## Provenance rules

- Every effective value can explain its source.
- AI proposals retain provider, model, request, confidence, and review state.
- Imported values retain source-asset identity.
- Locked values are not silently replaced.
- Editing a source exposes affected dependants before propagation.
- History records both the previous and new state.

## Asset knowledge

Images, audio, video, scripts, and documents are assets with versions and relationships to domain entities. Generated and imported assets do not become the sole representation of character or location knowledge.

## Prompt specifications

Prompts are rendered outputs of structured production knowledge. Store the prompt specification, provider-neutral intent, rendered provider form, negative prompt, inputs, and output relationship. A prompt is reproducible output, not the core domain model.
