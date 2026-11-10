package com.example.alexandria.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record HomeworkDTO(
        long id,
        @JsonProperty("subject_id") long subjectId,
        @JsonProperty("group_id") long groupId,
        @JsonProperty("teacher_id") long teacherId,
        String title,
        String description,
        String deadline
) {
}
