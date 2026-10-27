package com.example.alexandria;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Testcontainers
class MigrationIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void upgradesExistingVersionTwoSchemaAndPreservesData() throws Exception {
        Flyway baseline = Flyway.configure().dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .target(MigrationVersion.fromVersion("2")).load();
        baseline.migrate();
        try (var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO students(full_name, login, password, group_name) VALUES ('Student One', 'student', 'hashed-password', 'A1')");
            statement.executeUpdate("INSERT INTO groups(name) VALUES ('A1')");
            statement.executeUpdate("INSERT INTO teachers(full_name, login, password) VALUES ('Teacher One', 'teacher', 'hashed-password')");
            statement.executeUpdate("INSERT INTO subjects(subject_name) VALUES ('Math')");
            statement.executeUpdate("INSERT INTO schedule(day_of_week, lesson_num, week_type, subject_id, group_id, teacher_id) VALUES ('MONDAY', 1, 1, 1, 1, 1)");
            statement.executeUpdate("INSERT INTO homework(title, description, deadline, group_id, subject_id, teacher_id) VALUES ('Task', 'Work', '2030-01-01 12:00:00', 1, 1, 1)");
            statement.executeUpdate("INSERT INTO homework_status(student_id, homework_id, is_done, marked_at) VALUES (1, 1, true, '2024-01-01 12:00:00')");
        }
        Flyway latest = Flyway.configure().dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()).load();
        latest.migrate();
        assertThat(latest.info().current().getVersion().getVersion()).isEqualTo("3");
        try (var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT s.login, hs.marked_at AT TIME ZONE 'UTC', h.title, g.name, sub.subject_name, t.login FROM homework_status hs JOIN students s ON s.id=hs.student_id JOIN homework h ON h.id=hs.homework_id JOIN groups g ON g.id=h.group_id JOIN subjects sub ON sub.id=h.subject_id JOIN teachers t ON t.id=h.teacher_id")) {
            assertThat(rows.next()).isTrue();
            assertThat(rows.getString(1)).isEqualTo("student");
            assertThat(rows.getTimestamp(2).toLocalDateTime().toString()).isEqualTo("2024-01-01T12:00");
            assertThat(rows.getString(3)).isEqualTo("Task");
            assertThat(rows.getString(4)).isEqualTo("A1");
            assertThat(rows.getString(5)).isEqualTo("Math");
            assertThat(rows.getString(6)).isEqualTo("teacher");
            try (var generated = statement.executeQuery("INSERT INTO groups(name) VALUES ('B1') RETURNING id")) {
                assertThat(generated.next()).isTrue();
                assertThat(generated.getLong(1)).isGreaterThan(1);
            }
        }
    }
}
