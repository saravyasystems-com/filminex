# ADR-0002: JVM application stack and build system

- **Status:** Accepted
- **Date:** 2026-07-18
- **Decision owners:** Filminex maintainers

## Context

Filminex needs a durable application baseline for a knowledge-rich filmmaking workspace. The backend must support structured domain models, APIs, persistence, authentication, auditability, background work, and provider-independent integrations without coupling the product to any AI provider.

The repository is starting from a documentation-first foundation, so the first stack decision should favor a current long-term-support runtime, strong framework conventions, repeatable builds, and room for a modular architecture.

## Decision

Use the following baseline for the first Filminex backend application:

- **Language:** Java 25 LTS
- **JDK distribution:** any compatible OpenJDK distribution; builds must not depend on Oracle-specific APIs
- **Framework:** Spring Boot 4.1.x
- **Build system:** Gradle 9.6.x using the Gradle Wrapper
- **Build scripts:** Kotlin DSL
- **Architecture style:** modular monolith with explicit domain boundaries
- **API style:** HTTP/JSON, designed contract-first where external contracts become consequential
- **Testing:** JUnit 6 through Spring Boot test support
- **Dependency versions:** governed centrally by the Spring Boot dependency-management baseline and Gradle version catalogs where needed

Patch releases may advance within the selected Java, Spring Boot, and Gradle lines without a new ADR. Changing a major framework version, Java LTS line, or build system requires a superseding ADR.

## Rationale

Java 25 is the current Java LTS line and gives the project a long support runway. Spring Boot 4.1 supports Java 25 and provides the production conventions needed by the platform. Gradle supports Java 25, multi-project builds, build caching, and modular growth while its wrapper keeps developer and CI builds reproducible.

Kotlin DSL provides typed build configuration without introducing Kotlin as an application language requirement.

A modular monolith keeps early delivery and operations simple while enforcing boundaries that can later become independently deployable services if evidence requires it.

## Consequences

### Positive

- A current LTS runtime with a long maintenance horizon.
- Reproducible local and CI builds through the checked-in wrapper.
- Strong support for web APIs, validation, persistence, security, observability, and testing.
- Module boundaries can express Filminex domains without premature distributed systems.
- AI capabilities remain adapters rather than architectural dependencies.

### Costs and constraints

- Spring Boot 4 uses the Jakarta namespace and requires libraries compatible with its framework generation.
- Java 25 and Gradle 9 require current developer and CI environments.
- Kotlin DSL adds a small build-script learning cost.
- Modular boundaries must be verified continuously; directory structure alone is insufficient.

## Guardrails

- Domain code must not depend directly on AI-provider SDKs.
- Framework-specific types must not leak into core domain models unless justified.
- Cross-module access must occur through explicit public APIs.
- No module becomes a network service merely to imitate a service-oriented architecture.
- The Gradle Wrapper is the only supported project build entrypoint.
