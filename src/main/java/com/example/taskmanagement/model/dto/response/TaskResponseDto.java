package com.example.taskmanagement.model.dto.response;

import com.example.taskmanagement.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


public record TaskResponseDto (
        UUID id,
        String name,
        String description,
        TaskStatus status,
        UUID projectId,
        String projectName,
        String createdAt,
        String updatedAt){

}
