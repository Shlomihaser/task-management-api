package com.example.taskmanagement.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    @NotBlank(message = "Project name is required")
    private String name;
    private String description;
}
