# Repository and Package Design

**Status:** Implemented Sprint 0 foundation

The repository contains `apps/filminex-api`, `apps/filminex-web`, shared architecture and
sprint documentation, Compose infrastructure, and one CI workflow. Gradle owns the Java
build; npm owns the frontend build.

Java capability modules use `com.saravyasystems.filminex.<module>.api` for public
contracts and package-private `internal` implementations. Cross-module dependencies must
follow [Module Dependencies](module-dependencies.md) and are enforced with ArchUnit.

Schema changes are owned by the API's ordered Flyway migrations. Frontend code cannot
become a business-policy authority. Generated directories (`build`, `dist`,
`node_modules`, coverage, and TypeScript build metadata) are not committed.
