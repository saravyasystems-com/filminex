# Chapter 8 — AI and Reverse Population

## AI boundary

AI is accessed only through the AI Engine. The initial provider is Grok. Provider adapters isolate authentication, model options, request rendering, response normalization, safety metadata, usage, and errors.

Filminex domain services submit provider-neutral tasks. No module calls Grok directly.

## Optional capability

Projects and subscriptions can operate without AI or animation. When AI is unavailable, manual creation, structured editing, real-production planning, knowledge resolution, and collaboration continue to work.

## Reverse population

AI can propose structured data from existing creative material.

### Script input

A script can propose:

- story metadata;
- episodes;
- scenes;
- shots;
- frames;
- characters and locations;
- dialogue, narration, and production cues.

The requested breakdown depth controls how far the proposal goes.

### Character references

One or more images can propose face, physique, hair, eyes, complexion, apparent age, height where supportable, body shape, distinguishing marks, and other visible identity properties.

Directional references may be proposed as front, back, left, and right views. Generated views remain derived assets and must not be misrepresented as observed facts.

### Location references

Images can propose a location gist, visible attributes, time and weather cues, and directional references including front, back, left, right, top, and bottom.

### Context-sensitive interpretation

An image containing people is interpreted according to its import target:

- Character import → character proposals;
- Location import → location proposals;
- Scene import → scene gist and contextual proposals.

## Review lifecycle

```text
Input → Proposal → Review → Correct/Accept/Reject → Lock → Use
```

Proposals record confidence and provenance. AI does not silently overwrite locked or approved data.

## Generation

Provider-neutral prompt specifications combine the resolved production context, director notes, constraints, negative prompts, reference assets, and output requirements. Provider adapters render the final request.
