package com.example.alexandria.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AnnouncementDTO (
    long id,
    String title,
    String content,
    @JsonProperty("author_id") long authorId,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt
) {
}
