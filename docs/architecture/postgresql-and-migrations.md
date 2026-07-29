# PostgreSQL and Migration Foundation

## Authority

PostgreSQL is the authoritative system of record under ADR-0005. Flyway is the
only supported mechanism for changing the application schema.

## Local topology

The root `compose.yaml` starts PostgreSQL for local development. Configuration is
provided through `FILMINEX_DB_*` environment variables; `.env.example` documents
the supported values. The default credentials are for local development only.

The application owns the `filminex` PostgreSQL schema. Flyway creates it, records
its history in `filminex.flyway_schema_history`, validates migrations at startup,
and fails application startup if validation or migration fails.

## Migration rules

- Add forward-only, versioned SQL files under
  `apps/filminex-api/src/main/resources/db/migration`.
- Never edit a migration after it has reached `main`.
- Correct an applied schema through a new migration.
- Keep PostgreSQL as the authoritative state; projections such as Solr must be
  rebuildable from it.
- Use application-generated UUID identifiers so records can be referenced before
  persistence and across module boundaries.
- Store timestamps with time zone and interpret them as UTC.

## Initial schema

`V1__create_workspace_and_project_foundation.sql` establishes:

- `workspace`, the tenancy and ownership boundary;
- `project`, a workspace-owned filmmaking project;
- referential integrity and a workspace-scoped unique project name;
- an index supporting project lookup by workspace.

Deeper story, media, localization, rights, audit, and outbox schemas belong to
their respective workstreams.

## Verification

CI starts a real PostgreSQL 17 service, launches the Spring application context,
allows Flyway to migrate the database, and asserts:

- one migration completed successfully;
- the `workspace` table exists;
- the `project` table exists.

The same application context also activates Spring Boot's database health
indicator. A running application exposes it through `/actuator/health`.

## Local commands

```bash
cp .env.example .env
docker compose up -d postgres
./gradlew :apps:filminex-api:bootRun
curl http://localhost:8080/actuator/health
```

Stop the service without deleting local data:

```bash
docker compose down
```
