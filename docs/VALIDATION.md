# Validation record

On 2026-10-27, with Java 17 and a Docker daemon available, the following command completed successfully:

```sh
./gradlew clean check jacocoTestReport
```

The `test` task executed 25 tests: 15 service/password unit tests, nine Spring Boot API integration tests and one Flyway upgrade test. There were zero failures and zero skipped tests. `check` included Checkstyle and the per-service JaCoCo line coverage gate. The XML report recorded: Announcement 100%, Group 96.8%, Homework 71.4%, HomeworkStatus 78.6%, Schedule 100%, Student 98.1%, Subject 97.0% and Teacher 98.7%.

Some services exceed 80% because useful CRUD, validation and regression cases were retained; the 70–80% range is a target, not a reason to remove useful tests.

The PostgreSQL tests cover clean startup, upgrade from Flyway version 2 while retaining data, homework creation and reconciliation, repeated completion, group transfer, concurrent reconciliation, input validation, credential response handling and bulk rollback. The reports are generated locally under `build/reports/`.

GitHub Actions and Jenkins configuration are included, but local execution does not prove either hosted pipeline has run. Sonar requires a configured server and credentials; no Sonar result is claimed here. Previously exposed credentials must be rotated by their owner; source changes cannot invalidate them.

Existing timestamp columns are PostgreSQL timestamps without time zone. Migration and deployment assume their existing values represent UTC; verify that assumption against the source database before upgrading.

Before applying V3 to an existing database, back it up, inspect Flyway history and confirm both its schema and the UTC interpretation of stored timestamps. Test the upgrade on a copy first.

Docker smoke: `docker compose -p alexandria-assignment-smoke --env-file .env.example -f docker-compose.yml -f /tmp/alexandria-smoke-compose.yml up -d --build` built the application and started a new PostgreSQL database, bound to localhost port 18087. The documented create/retrieve/status flow passed with separate fixture names; two mark requests returned identical status and timestamp. `PUT /api/homework/2` followed by `GET /api/homework/search/id/2` confirmed the updated title. `docker compose ... down --volumes` removed only this temporary project and its dedicated volume.
