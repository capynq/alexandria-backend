package com.example.alexandria.service.dto;

import lombok.Builder;

@Builder
public record SubjectDTO(
        long id,
        @com.fasterxml.jackson.annotation.JsonProperty("subject_name") String subjectName
) {
}
