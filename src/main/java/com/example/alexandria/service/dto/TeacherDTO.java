package com.example.alexandria.service.dto;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Builder
public record TeacherDTO(
        long id,
        @com.fasterxml.jackson.annotation.JsonProperty("full_name") String fullName,
        @com.fasterxml.jackson.annotation.JsonProperty("is_admin") boolean isAdmin,
        String login,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        List<GroupDTO> groups,
        List<SubjectDTO> subjects) {

}
