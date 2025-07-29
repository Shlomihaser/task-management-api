package com.example.taskmanagement.model.dto.response;

public record ProjectResponseDto(
        String id,
        String name,
        String description,
        long taskCount,
        String createdAt,
        String updatedAt
) {
}
