# Deployment and Environments

**Status:** Implemented local/CI foundation

Sprint 0 defines one backend deployable and one independently buildable frontend. Local
development and CI use PostgreSQL 17 and Solr 9.8.1. Production hosting, topology,
autoscaling, CDN, backup schedules, and disaster recovery are later delivery decisions.

## Environment contract

- Java 25 runs the Spring Boot API; Node.js 24 builds the frontend.
- `FILMINEX_DB_*` configures PostgreSQL without embedding credentials.
- `FILMINEX_SOLR_URL` identifies the rebuildable Solr core.
- AI provider secrets are server-side and absent from audit records and browser bundles.
- Flyway runs forward-only migrations before the application becomes ready.

## Local startup

1. Copy `.env.example` to `.env` and start `docker compose up -d`.
2. Start the API with `./gradlew :apps:filminex-api:bootRun`.
3. Start the web shell with `npm run dev` under `apps/filminex-web`.
4. Verify `/actuator/health`; PostgreSQL and Solr dependencies must be healthy.

CI recreates this foundation for every pull request, initializes the Solr core, runs the
backend suite, and runs frontend tests plus a production build.
