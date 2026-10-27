package com.example.alexandria.service.dto;


import lombok.Builder;

@Builder
public record ScheduleDTO(
        long id,
        @com.fasterxml.jackson.annotation.JsonProperty("subject_id") long subjectId,
        @com.fasterxml.jackson.annotation.JsonProperty("group_id") long groupId,
        @com.fasterxml.jackson.annotation.JsonProperty("teacher_id") long teacherId,
        @com.fasterxml.jackson.annotation.JsonProperty("day_of_week") String dayOfWeek,
        @com.fasterxml.jackson.annotation.JsonProperty("lesson_num") int lessonNum,
        @com.fasterxml.jackson.annotation.JsonProperty("week_type") int weekType
) {}

