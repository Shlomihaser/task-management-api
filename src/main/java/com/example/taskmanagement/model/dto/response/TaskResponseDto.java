package com.example.taskmanagement.model.dto.response;

import com.example.taskmanagement.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private UUID id;
    private String name;
    private String description;
    private TaskStatus status;
    private UUID projectId;
    private String projectName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
