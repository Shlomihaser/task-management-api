package com.example.taskmanagement.model.dto.requests;

import com.example.taskmanagement.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {

    @NotBlank(message = "Task name is required")
    private String name;
    private String description;

    @NotBlank(message = "Project ID is required")
    private String projectId;

    private TaskStatus status = TaskStatus.TODO; // Default status
}