package com.example.alexandria.service.dto;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
public record StudentDTO(
        long id,
        @com.fasterxml.jackson.annotation.JsonProperty("full_name") String fullName,
        String login,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        @com.fasterxml.jackson.annotation.JsonProperty("group_name") String groupName
) {
}
