package com.example.alexandria.service.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AnnouncementDTO (
    long id,
    String title,
    String content,
    @com.fasterxml.jackson.annotation.JsonProperty("author_id") long authorId,
    @com.fasterxml.jackson.annotation.JsonProperty("created_at") Instant createdAt,
    @com.fasterxml.jackson.annotation.JsonProperty("updated_at") Instant updatedAt
) {
}
