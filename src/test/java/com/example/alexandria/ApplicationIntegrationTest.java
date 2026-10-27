package com.example.alexandria;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;
    long groupId;
    long otherGroupId;
    long subjectId;
    long teacherId;

    @BeforeEach
    void fixtures() {
        jdbc.execute("TRUNCATE homework_status, homework, schedule, groups_teachers, subjects_teachers, announcements, students, teachers, subjects, groups RESTART IDENTITY CASCADE");
        groupId = jdbc.queryForObject("INSERT INTO groups(name) VALUES ('A1') RETURNING id", Long.class);
        otherGroupId = jdbc.queryForObject("INSERT INTO groups(name) VALUES ('B1') RETURNING id", Long.class);
        subjectId = jdbc.queryForObject("INSERT INTO subjects(subject_name) VALUES ('Math') RETURNING id", Long.class);
        teacherId = jdbc.queryForObject("INSERT INTO teachers(full_name, login, password) VALUES ('Teacher One', 'teacher', 'hashed-password') RETURNING id", Long.class);
        jdbc.update("INSERT INTO students(id, full_name, login, password, group_name) VALUES (200, 'Student One', 'student', 'hashed-password', 'A1'), (201, 'Student Two', 'other', 'hashed-password', 'B1')");
    }

    private String homework(long group) {
        return "{\"subject_id\":" + subjectId + ",\"group_id\":" + group + ",\"teacher_id\":" + teacherId + ",\"title\":\"Algebra\",\"description\":\"Exercises\",\"deadline\":\"2030-01-01T12:00:00Z\"}";
    }

    @Test
    void homeworkCreatesStatusesAndCompletionIsIdempotentForLargeStudentId() throws Exception {
        String created = mvc.perform(post("/api/homework").contentType(MediaType.APPLICATION_JSON).content(homework(groupId)))
                .andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.title").value("Algebra"))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();
        mvc.perform(get("/api/homework/search/id/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.group_id").value(groupId));
        mvc.perform(get("/api/homework-status/student/200")).andExpect(status().isOk()).andExpect(jsonPath("$[0].homework_id").value(id)).andExpect(jsonPath("$[0].is_done").value(false));
        String first = mvc.perform(put("/api/homework-status/mark/{homework}/{student}", id, 200)).andExpect(status().isOk()).andExpect(jsonPath("$.is_done").value(true)).andReturn().getResponse().getContentAsString();
        String second = mvc.perform(put("/api/homework-status/mark/{homework}/{student}", id, 200)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode a = json.readTree(first), b = json.readTree(second);
        assertThat(a.get("marked_at").asText()).isEqualTo(b.get("marked_at").asText());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM homework_status WHERE homework_id = ?", Integer.class, id)).isEqualTo(1);
        mvc.perform(put("/api/homework-status/mark/{homework}/{student}", id, 201)).andExpect(status().is4xxClientError());
        mvc.perform(put("/api/homework/{id}", id).contentType(MediaType.APPLICATION_JSON).content(homework(otherGroupId))).andExpect(status().isConflict());
    }

    @Test
    void readsReconcileNewStudentsAndScheduleUsesGroupName() throws Exception {
        String created = mvc.perform(post("/api/homework").contentType(MediaType.APPLICATION_JSON).content(homework(groupId))).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();
        jdbc.update("INSERT INTO students(id, full_name, login, password, group_name) VALUES (202, 'Student Three', 'third', 'hashed-password', 'A1')");
        mvc.perform(get("/api/homework-status/student/202")).andExpect(status().isOk()).andExpect(jsonPath("$[0].homework_id").value(id));
        jdbc.update("INSERT INTO schedule(day_of_week, lesson_num, week_type, subject_id, group_id, teacher_id) VALUES ('MONDAY', 1, 1, ?, ?, ?)", subjectId, groupId, teacherId);
        mvc.perform(get("/api/schedule/groups/A1")).andExpect(status().isOk()).andExpect(jsonPath("$[0].group_id").value(groupId));
    }

    @Test
    void credentialsAreNotReturnedAndInvalidRequestsAreRejected() throws Exception {
        String create = "{\"full_name\":\"Student Four\",\"login\":\"fourth\",\"password\":\"secret123\",\"group_name\":\"A1\"}";
        String response = mvc.perform(post("/api/students/create").contentType(MediaType.APPLICATION_JSON).content(create))
                .andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.password").doesNotExist()).andReturn().getResponse().getContentAsString();
        assertThat(response).doesNotContain("secret123", "$2a$", "$2b$");
        mvc.perform(post("/api/students/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"fourth\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(post("/api/students/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"fourth\",\"password\":\"wrong123\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/homework").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/homework/search/id/999999")).andExpect(status().isNotFound());
    }
}
