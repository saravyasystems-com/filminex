# Chapter 4 — Workspace Experience

## Workspace, not module maze

Users enter a filmmaking workspace and work in context. Crafts can have perspectives, but the product should not make users navigate unrelated modules to complete one scene.

## Core interaction model

- The hierarchy provides navigation and structure.
- Tables or lists provide compact overviews.
- Selecting a row opens its properties in a consistent context inspector.
- An information indicator explains inherited values.
- Users can navigate directly to the entity that supplied a value.
- Read mode presents the work as a coherent story.
- Write access enables controlled refinement.

Hover behavior remains minimal. Essential information must not depend on hover.

## Progressive disclosure

Properties and tools appear when their entity, craft, entitlement, and production mode make them relevant. “No use. No see.” is enforced by the experience and Capability Engine together.

## Inheritance experience

The interface shows:

- the effective value;
- whether it is declared, inherited, inferred, imported, or overridden;
- its source entity;
- whether it is locked;
- confidence when AI inferred it;
- the command to edit the source or create a local override.

The user should not have to understand a messy inheritance chain to make a safe change.

## Collaboration experience

A work can be shared in read mode. Approved collaborators may receive write access and refine content. Ownership, copied/shared state, and promotion of a shared copy remain explicit.

## AI experience

AI proposes structures and properties into the same reviewable workspace. It does not create a separate opaque representation of the production.
