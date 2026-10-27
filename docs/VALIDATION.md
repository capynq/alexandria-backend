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

The change set covers Gradle, tests and coverage, credential configuration, Docker, CI, application controllers/services/DTOs, Flyway V3 and documentation. At commit 545d701, `git diff --stat 20c37bc HEAD` reported 99 files changed (including removal of previously tracked generated artifacts). Use `git diff --stat 20c37bc HEAD` for the current file-level summary and `git log --oneline --reverse 20c37bc..HEAD` for the complete ordered commit list, including the final documentation commit.

Commits through the initial validation report:

```text
80f3ac2 chore: create assignment implementation branch
59e1e16 build: configure tests coverage and quality checks
38b7dd2 security: externalize database credentials
5ce4673 build: containerize application and database
3c8bdcf ci: run verification and publish reports
d70c73c feat: stabilize domain services and demo API flows
dd8d8c9 test: cover services API and PostgreSQL migrations
9de5012 ci: bind optional Sonar token from Jenkins credentials
cb860ed fix: prevent record overwrite and stabilize status reconciliation
e50c824 build: use pinned Gradle image distribution
ecb5dfa fix: configure allowed browser origins
91ec3ab docs: document setup architecture and demo
4e85e86 fix: preserve completion timestamp precision
545d701 docs: record quality and validation evidence
```

JaCoCo service line counts (covered / total): Announcement 37/37, Group 30/31, Homework 35/49, HomeworkStatus 22/28, Schedule 47/47, Student 51/52, Subject 32/33, Teacher 74/75. The XML is `build/reports/jacoco/test/jacocoTestReport.xml`; HTML starts at `build/reports/jacoco/test/html/index.html`.

Final sequential verification used Zulu Java 17 and `/tmp/alexandria-gradle`: `./gradlew clean build --no-daemon` passed (six tasks from cache); `./gradlew test --rerun-tasks --no-daemon` passed (25 tests, no failures, errors or skips); `./gradlew check jacocoTestReport jacocoTestCoverageVerification --no-daemon` passed. Docker/network access required escalation in this environment.
