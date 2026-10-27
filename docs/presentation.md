# Alexandria backend

## Problem

Coordinate educational groups, schedules, homework and completion records through a consistent HTTP API.

---

## Design

- Spring Boot controllers and service layer
- Spring Data JPA with PostgreSQL
- Flyway migrations and schema validation
- bcrypt password verification

---

## Homework workflow

1. Create an assignment for a group, subject and teacher.
2. Create or reconcile status records for students in the group.
3. Mark a student's assignment complete; repeated requests retain the timestamp.

---

## Verification

- Unit tests exercise services and password handling.
- PostgreSQL integration tests exercise the API and migrations using Testcontainers.
- Gradle provides JaCoCo and Checkstyle; CI runs the configured verification tasks.

---

## Limits and next steps

Login verifies credentials but does not issue a token or session. Endpoint authorization is not implemented. Add an authentication and authorization model before public deployment, and verify migration and CI results in the target environment.
