# Frontend Foundation

**Status:** Implemented foundation

**Workstream:** Sprint 0 — 13/14

**Decisions:** [ADR-0002](decisions/0002-product-principles.md),
[ADR-0016](decisions/0016-capability-driven-product.md),
[ADR-0022](decisions/0022-sprint-0-scope.md)

## Boundary

`apps/filminex-web` is the browser application boundary. React renders the workspace,
TypeScript keeps presentation contracts explicit, and Vite owns local development and
production bundling. The application is independently buildable from the Java backend.

Sprint 0 proves four experience contracts:

1. workspace and project context remain visible;
2. navigation omits capabilities the workspace cannot use;
3. hierarchy selection updates one consistent context inspector; and
4. provenance and editability are explained at the property boundary.

These contracts express “Context is the interface,” “No use. No see.,” and “Explain
before automate” without freezing a finished visual system.

## State and API seam

The shell currently consumes deterministic local data. Presentation state is split into:

- workspace context: workspace, project, role, and available capabilities;
- navigation state: the current perspective;
- selection state: the entity shown by the inspector.

Backend transport, authentication, generated API types, caching, and error conventions
remain deferred until their contracts are established. Capability visibility in the
browser is a usability rule, not an authorization boundary; the backend remains
authoritative.

## Verification

`npm test` verifies capability filtering and role-derived editability. `npm run build`
performs strict TypeScript compilation and creates the production bundle. GitHub Actions
runs both commands alongside the backend verification.

## Deferred

- live API and authentication integration;
- full story and production-library editors;
- routing beyond the foundation shell;
- collaborative state and conflict handling;
- component library, accessibility audit, and visual-regression infrastructure;
- offline behavior, telemetry, and deployment configuration.
