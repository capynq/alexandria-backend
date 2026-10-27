package com.example.alexandria.service.dto;

import lombok.Builder;

@Builder
public record HomeworkStatusDTO(
        long id,
        @com.fasterxml.jackson.annotation.JsonProperty("homework_id") long homeworkId,
        @com.fasterxml.jackson.annotation.JsonProperty("student_id") long studentId,
        @com.fasterxml.jackson.annotation.JsonProperty("is_done") boolean isDone,
        @com.fasterxml.jackson.annotation.JsonProperty("marked_at") String markedAt
) {
}
