package com.example.alexandria.service.dto;

import lombok.Builder;


@Builder
public record GroupDTO(
        Long id,
        String name
) {
}

