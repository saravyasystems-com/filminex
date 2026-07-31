# Test Strategy

**Status:** Implemented Sprint 0 foundation

The foundation uses the smallest test level that proves each boundary:

- unit tests cover deterministic policies and provider adapters;
- integration tests use PostgreSQL for migrations, isolation, concurrency, and audit;
- Solr tests prove workspace filtering and projection behavior;
- ArchUnit tests enforce package visibility, allowed dependencies, and acyclicity;
- frontend unit tests prove capability visibility and role-derived editability;
- frontend production build performs strict TypeScript compilation.

GitHub Actions is authoritative because it provisions Java 25, PostgreSQL, Solr, and
Node.js 24 together. Every pull request runs the complete backend suite, frontend tests,
frontend build, and repository-wide whitespace validation. Regression tests accompany
defect fixes. Browser E2E, load, accessibility, security, and production smoke suites are
deferred until their runtime surfaces exist.
