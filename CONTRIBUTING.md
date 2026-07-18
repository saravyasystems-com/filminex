# Contributing to Filminex

## Current phase

Filminex is in Genesis / Sprint 0. Contributions should clarify the product model, capture decisions, or establish a thin, reversible technical foundation.

## Workflow

1. Create a focused branch from `main`.
2. Keep each change small enough to review independently.
3. Record consequential architectural choices as ADRs.
4. Add tests with implementation changes when a stack is introduced.
5. Open a pull request explaining the user outcome, scope, verification, and follow-ups.

## Branch and commit conventions

- Branches: `feature/<name>`, `fix/<name>`, `docs/<name>`, `chore/<name>`
- Commits: use concise Conventional Commit prefixes such as `feat:`, `fix:`, `docs:`, `test:`, and `chore:`

## Product guardrails

- Keep AI providers behind abstractions.
- Do not move character identity properties into scene state.
- Wardrobe is selected per character per scene.
- Inherited values must retain provenance.
- New modules must respect entitlement and “No use. No see.” visibility.
- Do not commit secrets, generated media, or private production assets.

## Definition of done

A change is done when its intended behavior is documented, verified, reviewable, and does not leave unexplained architecture decisions.
