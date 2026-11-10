package com.example.alexandria.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SubjectDTO(
        long id,
        @JsonProperty("subject_name") String subjectName
) {
}
