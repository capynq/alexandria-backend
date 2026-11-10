package com.example.alexandria.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record HomeworkStatusDTO(
        long id,
        @JsonProperty("homework_id") long homeworkId,
        @JsonProperty("student_id") long studentId,
        @JsonProperty("is_done") boolean isDone,
        @JsonProperty("marked_at") String markedAt
) {
}
