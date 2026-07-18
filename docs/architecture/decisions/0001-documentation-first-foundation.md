# ADR-0001: Documentation-first repository foundation

- Status: Accepted
- Date: 2026-07-18

## Context

Filminex has a broad product domain and several plausible implementation stacks. Selecting frameworks before stabilizing core boundaries could turn product assumptions into expensive technical constraints.

## Decision

Initialize the repository with the product charter, principles, domain model, roadmap, contribution rules, security baseline, and ADR process. Defer application scaffolding until the first stack ADR is approved.

## Consequences

- Product vocabulary and guardrails become reviewable immediately.
- The implementation stack remains reversible.
- No runnable application exists at this stage.
- Sprint 0 must conclude with explicit stack, tenancy, persistence, and asset decisions before feature implementation accelerates.
