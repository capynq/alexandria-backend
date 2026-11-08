# Alexandria backend

Java 17 Spring Boot API for groups, students, teachers, schedules, announcements and homework. PostgreSQL stores the data; Flyway applies migrations and Hibernate validates the schema.

## Start

1. Install Java 17 and Docker with Compose.
2. Set local credentials and start the stack:

   ```sh
   export DB_NAME=alexandria
   export DB_USERNAME=alexandria
   export DB_PASSWORD=local-password
   export DB_URL=jdbc:postgresql://db:5432/alexandria
   docker compose up --build
   ```


To run the application outside Compose, point `DB_URL` at an accessible PostgreSQL instance, set `DB_USERNAME` and `DB_PASSWORD`, then run `./gradlew bootRun`. The Compose database URL uses the container hostname `db` and is not a host URL.

## Verify

```sh
./gradlew unitTest
./gradlew integrationTest
./gradlew clean check jacocoTestReport
```

Integration tests require a working Docker daemon and use a disposable PostgreSQL container. `test` includes both unit and integration tests; `unitTest` excludes integration-tagged tests and `integrationTest` selects them. HTML coverage is generated at `build/reports/jacoco/test/html/index.html`. The configured coverage check requires at least 70% line coverage per service class. CI runs `clean check jacocoTestReport` and publishes reports. Sonar analysis requires a separately configured server, project and credentials; CI does not imply that an analysis has run.

The Jenkins pipeline can optionally run Sonar with `SONAR_URL` and `SONAR_CREDENTIAL_ID`. The credential ID must refer to a secret-text token; the Jenkins Credentials Binding plugin exposes it as `SONAR_TOKEN` for analysis.

## API and limitations

Request fields retain snake_case names. The examples cover creation, retrieval and idempotent homework completion. Passwords are accepted for create and login but omitted from responses. Credential verification does not issue a token or session, and endpoint authorization is not implemented. Do not expose this service to untrusted networks without adding authorization.

Browser access defaults to the origin `http://localhost:3000`. Set `APP_CORS_ALLOWED_ORIGINS` to a comma-separated list of explicitly trusted origins for another frontend. `GET /api/students` returns an unfiltered list and rejects a `password` query parameter; submit credentials in the login request body.