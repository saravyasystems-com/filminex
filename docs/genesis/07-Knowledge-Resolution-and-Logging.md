# Chapter 7 — Knowledge, Resolution, and Logging

## Saravya Knowledge Engine

The Knowledge Engine maintains the graph of production meaning:

- containment and hierarchy;
- library usage;
- character participation;
- location and wardrobe assignment;
- property origin and dependency;
- assets and versions;
- prompts and outputs;
- collaboration and ownership.

It enables navigation, dependency discovery, search, and impact analysis.

## Resolution Engine

The Resolution Engine computes effective property values from definitions, scope, inheritance, locks, and overrides. Resolution is deterministic and explainable.

A resolution result includes the value, source, path, rule applied, and any conflict or missing required assignment.

## Logging Engine

Logging is part of the knowledge architecture, not an afterthought. It captures:

- user commands and mutations;
- AI requests, proposals, acceptances, edits, and rejections;
- system events and background processing;
- authentication, authorization, and entitlement decisions;
- property resolution and provenance changes;
- asset creation, replacement, and deletion;
- sharing and permission changes;
- failures and recovery actions.

## Audit versus operational logs

Audit events describe meaningful state transitions and are durable. Operational logs diagnose execution and follow an appropriate retention policy. Analytics may consume events later but must not redefine their original meaning.

## Explainability

From any visible property, the user can discover:

1. its effective value;
2. where it originated;
3. how it was resolved;
4. who or what changed it;
5. what depends on it;
6. its previous versions.

This is the foundation for “Their software makes complex knowledge explainable.”
