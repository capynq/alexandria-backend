package com.example.alexandria.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Builder;

@Builder
public record ScheduleDTO(
        long id,
        @JsonProperty("subject_id") long subjectId,
        @JsonProperty("group_id") long groupId,
        @JsonProperty("teacher_id") long teacherId,
        @JsonProperty("day_of_week") String dayOfWeek,
        @JsonProperty("lesson_num") int lessonNum,
        @JsonProperty("week_type") int weekType
) {}

