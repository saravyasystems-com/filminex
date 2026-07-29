# Filminex API

The first runnable Filminex backend application.

## Requirements

- Java 25
- Docker with Compose

Gradle is supplied through the repository wrapper.

## Verify

```shell
docker compose up -d postgres
./gradlew test
```

## Run

```shell
cp .env.example .env
docker compose up -d postgres
./gradlew :apps:filminex-api:bootRun
```

Flyway migrates the database during application startup. The health endpoint,
including the database component, is available at
`http://localhost:8080/actuator/health`.
