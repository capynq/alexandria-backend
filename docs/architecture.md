# Architecture

Alexandria is a Java 17 Spring Boot REST service for students, teachers, groups, subjects, schedules, announcements and homework. Controllers accept JSON DTOs, services implement the operations, Spring Data repositories persist entities, and PostgreSQL stores the data. Flyway owns schema changes; Hibernate validates the schema on startup.

Homework belongs to a group, subject and teacher. Creating an assignment creates status rows for students currently in the group. Status reads reconcile missing rows for current group members. Completion checks membership and repeated completion preserves the existing completion timestamp. Changing a homework group after status rows exist is rejected with HTTP 409.

After a student transfers, historical statuses remain visible, but marking an assignment from the previous group returns HTTP 409.

Student and teacher login verifies a bcrypt password and returns the corresponding record. Passwords are write-only in JSON responses. Login does not create a session or token, and the application does not provide endpoint authorization; deploy only in a trusted environment until authorization is added.

Flyway migrations are in `src/main/resources/db/migration`. New installations apply the complete migration sequence. Existing installations must back up their database and verify Flyway history before upgrading. Automatic baselining cannot establish that an arbitrary existing schema matches the migrations.
