package com.example.alexandria.service.dto;

import lombok.Builder;

@Builder
public record HomeworkDTO(
        long id,
        @com.fasterxml.jackson.annotation.JsonProperty("subject_id") long subjectId,
        @com.fasterxml.jackson.annotation.JsonProperty("group_id") long groupId,
        @com.fasterxml.jackson.annotation.JsonProperty("teacher_id") long teacherId,
        String title,
        String description,
        String deadline
) {
}
