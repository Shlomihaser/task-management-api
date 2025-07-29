package com.example.taskmanagement.controllers;


import com.example.taskmanagement.annotations.CurrentUser;
import com.example.taskmanagement.exceptions.ProjectNotFoundException;
import com.example.taskmanagement.mappers.ProjectMapper;
import com.example.taskmanagement.model.dto.requests.ProjectRequestDto;
import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.dto.response.ProjectResponseDto;
import com.example.taskmanagement.model.entity.Project;


import com.example.taskmanagement.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponseDto>> listProjects(@CurrentUser String ownerId) {

        List<ProjectResponseDto> projects = projectService.listProjects(ownerId)
                .stream()
                .map(projectMapper::toDto)
                .toList();

        return ResponseEntity.ok(projects);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProjectResponseDto>> listProjectsPaginated(
            @CurrentUser String ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        // Create Pageable
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<Project> projects = projectService.listProjects(ownerId, pageable);
        // Map to DTOs
        List<ProjectResponseDto> projectDtos = projects.getContent().stream()
                .map(projectMapper::toDto)
                .toList();
                
        PagedResponse<ProjectResponseDto> response = PagedResponse.<ProjectResponseDto>builder()
                .content(projectDtos)
                .page(projects.getPage())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> getProjectById(@CurrentUser String ownerId,
                                                      @PathVariable String projectId) {
        Project foundProject = projectService.getProjectById(ownerId, projectId);
        ProjectResponseDto projectResponse = projectMapper.toDto(foundProject);
        return ResponseEntity.ok(projectResponse);
    }

    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(@CurrentUser String ownerId,
                                                     @Valid @RequestBody ProjectRequestDto projectRequest) {
        Project project = projectMapper.toEntity(projectRequest);
        Project newProject = projectService.createProject(ownerId, project);
        ProjectResponseDto projectResponse = projectMapper.toDto(newProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
    }

    @DeleteMapping("/{projectId}")
    ResponseEntity<String> deleteProject(@CurrentUser String ownerId,
                                         @PathVariable String projectId) {
        projectService.deleteProject(ownerId, projectId);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    @PutMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> updateProject(@CurrentUser String ownerId,
                                                     @PathVariable String projectId, @Valid @RequestBody ProjectRequestDto projectRequest) {
        Project project = projectMapper.toEntity(projectRequest);
        Project updatedProject = projectService.updateProject(ownerId, projectId, project);
        ProjectResponseDto projectResponse = projectMapper.toDto(updatedProject);
        return ResponseEntity.ok(projectResponse);
    }


}
