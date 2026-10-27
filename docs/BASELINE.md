# Baseline

The assignment branch starts from commit 20c37bc on main. The repository uses Gradle 8.5 and contains a Spring Boot application, Flyway migrations, and tracked generated build artifacts. Baseline verification uses Java 17.

In a detached checkout of 20c37bc, `./gradlew clean build` and `./gradlew test` both succeeded with Java 17. Gradle reported `test NO-SOURCE`: the original repository contained no tests. The original configuration included hard-coded credentials and tracked generated output; those credentials should be rotated because they remain in Git history.
