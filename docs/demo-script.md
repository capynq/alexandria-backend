# Demo script

1. Start PostgreSQL and the application using the README; open `docs/api-examples.md`.
2. Run the setup commands and show the returned group, subject, teacher and student IDs.
3. Create homework and retrieve it by ID. Show the student's incomplete status.
4. Mark the homework complete twice and compare the status and timestamp.
5. Run `./gradlew unitTest` and `./gradlew integrationTest` with Docker available. Show actual output and the JaCoCo report when generated; do not claim an unrun test passed.
