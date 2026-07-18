# Filminex API

The first runnable Filminex backend application.

## Requirements

- Java 25

Gradle is supplied through the repository wrapper.

## Verify

```shell
./gradlew test
```

## Run

```shell
./gradlew :apps:filminex-api:bootRun
```

The health endpoint is available at `http://localhost:8080/actuator/health`.
