# Chapter 10 — Delivery Boundary

## Genesis closure

Genesis is frozen and closed. Implementation proceeds through code, schema, API, UI, sprint artifacts, and ADRs only where a consequential technical choice must be recorded.

Routine delivery must not restart product philosophy.

## Sprint 0

The declared foundation includes:

- repository structure;
- Spring Boot platform skeleton;
- PostgreSQL foundation;
- identity and workspace boundary;
- Logging Engine foundation;
- Capability Engine foundation;
- provider-agnostic AI Engine with a Grok adapter;
- architecture tests and delivery conventions.

## Sprint 1 and beyond

Subsequent work introduces:

- Knowledge Engine;
- Resolution Engine;
- domain modules;
- workspaces and perspectives;
- story hierarchy and read mode;
- production libraries;
- reverse population;
- collaboration and entitlements.

Detailed sequencing is maintained as sprint artifacts rather than amendments to Genesis.

## Decision discipline

Use an ADR when a choice is expensive to reverse, crosses multiple modules, affects security or tenancy, or changes a declared architectural boundary.

An ADR states:

- the Genesis requirement it implements;
- context and options;
- the selected decision;
- consequences and migration implications;
- whether it conforms to or proposes a departure from Genesis.

## Definition of implementation alignment

A delivery aligns with Genesis when it:

- keeps domain rules out of infrastructure and shared platform code;
- preserves character identity versus scene context;
- keeps AI optional and provider-agnostic;
- retains provenance and logging;
- enforces capabilities in both UI and API;
- implements “No use. No see.”;
- makes production knowledge explainable.
