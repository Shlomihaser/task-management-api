package com.example.taskmanagement.controllers;


import com.example.taskmanagement.exceptions.ProjectNotFoundException;
import com.example.taskmanagement.mappers.ProjectMapper;
import com.example.taskmanagement.model.dto.requests.ProjectRequestDto;
import com.example.taskmanagement.model.dto.response.ProjectResponseDto;
import com.example.taskmanagement.model.entity.Project;


import com.example.taskmanagement.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> listProjects(@AuthenticationPrincipal Jwt principal) {
        String ownerId = principal.getSubject();

        List<ProjectResponseDto> projects = projectService.listProjects(ownerId)
                .stream()
                .map(projectMapper::toDto)
                .toList();

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> getProjectById(@AuthenticationPrincipal Jwt principal, @PathVariable String projectId) {
        String ownerId = principal.getSubject();
        Project foundProject = projectService.getProjectById(ownerId, projectId);
        ProjectResponseDto projectResponse = projectMapper.toDto(foundProject);
        return ResponseEntity.ok(projectResponse);
    }

    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(@AuthenticationPrincipal Jwt principal, @Valid @RequestBody ProjectRequestDto projectRequest) {
        String ownerId = principal.getSubject();
        Project project = projectMapper.toEntity(projectRequest);
        Project newProject = projectService.createProject(ownerId, project);
        ProjectResponseDto projectResponse = projectMapper.toDto(newProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
    }

    @DeleteMapping("/{projectId}")
    ResponseEntity<String> deleteProject(@AuthenticationPrincipal Jwt principal, @PathVariable String projectId) {
        String ownerId = principal.getSubject();
        projectService.deleteProject(ownerId, projectId);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    @PutMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> updateProject(@AuthenticationPrincipal Jwt principal, @PathVariable String projectId, @Valid @RequestBody ProjectRequestDto projectRequest) {
        String ownerId = principal.getSubject();

        // Create project entity from request
        Project project = projectMapper.toEntity(projectRequest);
        Project updatedProject = projectService.updateProject(ownerId, projectId, project);
        ProjectResponseDto projectResponse = projectMapper.toDto(updatedProject);
        return ResponseEntity.ok(projectResponse);
    }


}
