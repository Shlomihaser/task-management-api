package com.example.taskmanagement.services.impl;

import com.example.taskmanagement.exceptions.ProjectNotFoundException;
import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import com.example.taskmanagement.model.enums.TaskStatus;
import com.example.taskmanagement.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private String ownerId;
    private String projectIdStr;
    private UUID projectId;
    private Project project;

    @BeforeEach
    void setUp() {
        ownerId = "user-123";
        projectId = UUID.randomUUID();
        projectIdStr = projectId.toString();
        
        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Description")
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Test listProjects(String ownerId)
    @Test
    void listProjects_ShouldReturnProjectList_WhenOwnerIdProvided() {
        // Given
        List<Project> expectedProjects = Arrays.asList(project, createAnotherProject());
        when(projectRepository.findAllWithTasks(ownerId)).thenReturn(expectedProjects);

        // When
        List<Project> result = projectService.listProjects(ownerId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedProjects);
        verify(projectRepository).findAllWithTasks(ownerId);
    }

    @Test
    void listProjects_ShouldReturnEmptyList_WhenNoProjectsFound() {
        // Given
        when(projectRepository.findAllWithTasks(ownerId)).thenReturn(List.of());

        // When
        List<Project> result = projectService.listProjects(ownerId);

        // Then
        assertThat(result).isEmpty();
        verify(projectRepository).findAllWithTasks(ownerId);
    }

    // Test listProjects(String ownerId, Pageable pageable)
    @Test
    void listProjectsPaginated_ShouldReturnPagedResponse_WhenValidInput() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = Arrays.asList(project);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, 1);
        when(projectRepository.findAllWithTasksPaginated(ownerId, pageable)).thenReturn(projectPage);

        // When
        PagedResponse<Project> result = projectService.listProjects(ownerId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage().getTotalElements()).isEqualTo(1);
        assertThat(result.getPage().getTotalPages()).isEqualTo(1);
        verify(projectRepository).findAllWithTasksPaginated(ownerId, pageable);
    }

    @Test
    void listProjectsPaginated_ShouldReturnEmptyPage_WhenNoProjectsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(projectRepository.findAllWithTasksPaginated(ownerId, pageable)).thenReturn(emptyPage);

        // When
        PagedResponse<Project> result = projectService.listProjects(ownerId, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPage().getTotalElements()).isEqualTo(0);
        verify(projectRepository).findAllWithTasksPaginated(ownerId, pageable);
    }

    // Test createProject(String ownerId, Project project)
    @Test
    void createProject_ShouldReturnSavedProject_WhenValidInput() {
        // Given
        Project projectToCreate = Project.builder()
                .name("New Project")
                .description("New Description")
                .build();
        
        Project savedProject = Project.builder()
                .id(UUID.randomUUID())
                .name("New Project")
                .description("New Description")
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // When
        Project result = projectService.createProject(ownerId, projectToCreate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Project");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        assertThat(projectToCreate.getOwnerId()).isEqualTo(ownerId); // Verify ownerId was set
        
        verify(projectRepository).save(projectToCreate);
    }

    @Test
    void createProject_ShouldSetOwnerId_WhenProjectHasNullOwnerId() {
        // Given
        Project projectToCreate = Project.builder()
                .name("New Project")
                .ownerId(null) // explicitly null
                .build();
        
        Project savedProject = Project.builder()
                .id(UUID.randomUUID())
                .name("New Project")
                .ownerId(ownerId)
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // When
        Project result = projectService.createProject(ownerId, projectToCreate);

        // Then
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        assertThat(projectToCreate.getOwnerId()).isEqualTo(ownerId);
        verify(projectRepository).save(projectToCreate);
    }

    // Test getProjectById(String ownerId, String projectId)
    @Test
    void getProjectById_ShouldReturnProject_WhenProjectExists() {
        // Given
        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.of(project));

        // When
        Project result = projectService.getProjectById(ownerId, projectIdStr);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(project);
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
    }

    @Test
    void getProjectById_ShouldThrowProjectNotFoundException_WhenProjectNotFound() {
        // Given
        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getProjectById(ownerId, projectIdStr))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessage("Project not found with id: " + projectIdStr);
        
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
    }

    @Test
    void getProjectById_ShouldThrowIllegalArgumentException_WhenInvalidProjectId() {
        // Given
        String invalidProjectId = "invalid-uuid";

        // When & Then
        assertThatThrownBy(() -> projectService.getProjectById(ownerId, invalidProjectId))
                .isInstanceOf(IllegalArgumentException.class);
        
        verifyNoInteractions(projectRepository);
    }

    // Test deleteProject(String ownerId, String projectId)
    @Test
    void deleteProject_ShouldDeleteProject_WhenProjectExists() {
        // Given
        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.of(project));

        // When
        projectService.deleteProject(ownerId, projectIdStr);

        // Then
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_ShouldThrowProjectNotFoundException_WhenProjectNotFound() {
        // Given
        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.deleteProject(ownerId, projectIdStr))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessage("Project not found with id: " + projectIdStr);
        
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
        verify(projectRepository, never()).delete(any());
    }

    @Test
    void deleteProject_ShouldThrowIllegalArgumentException_WhenInvalidProjectId() {
        // Given
        String invalidProjectId = "invalid-uuid";

        // When & Then
        assertThatThrownBy(() -> projectService.deleteProject(ownerId, invalidProjectId))
                .isInstanceOf(IllegalArgumentException.class);
        
        verifyNoInteractions(projectRepository);
    }

    @Test
    void deleteProject_ShouldDeleteWithCascade_WhenProjectHasTasks() {
        // Given
        Task task1 = Task.builder()
                .id(UUID.randomUUID())
                .name("Task 1")
                .status(TaskStatus.TODO)
                .project(project)
                .build();
        
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .name("Task 2")
                .status(TaskStatus.IN_PROGRESS)
                .project(project)
                .build();
                
        project.setTasks(Arrays.asList(task1, task2));
        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.of(project));

        // When
        projectService.deleteProject(ownerId, projectIdStr);

        // Then
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
        verify(projectRepository).delete(project);
        // Note: Cascade deletion of tasks is handled by JPA/database, not in service layer
    }

    // Test updateProject(String ownerId, String projectId, Project project)
    @Test
    void updateProject_ShouldReturnUpdatedProject_WhenValidInput() {
        // Given
        Project existingProject = Project.builder()
                .id(projectId)
                .name("Old Name")
                .description("Old Description")
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();
        
        Project updateData = Project.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();
        
        Project updatedProject = Project.builder()
                .id(projectId)
                .name("Updated Name")
                .description("Updated Description")
                .ownerId(ownerId)
                .createdAt(existingProject.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        // When
        Project result = projectService.updateProject(ownerId, projectIdStr, updateData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
        verify(projectRepository).save(existingProject);
        
        // Verify the existing project was updated
        assertThat(existingProject.getName()).isEqualTo("Updated Name");
        assertThat(existingProject.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void updateProject_ShouldThrowProjectNotFoundException_WhenProjectNotFound() {
        // Given
        Project updateData = Project.builder().name("Updated Name").build();
        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.updateProject(ownerId, projectIdStr, updateData))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessage("Project not found with id: " + projectIdStr);
        
        verify(projectRepository).findByIdAndOwnerId(ownerId, projectId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_ShouldThrowIllegalArgumentException_WhenInvalidProjectId() {
        // Given
        String invalidProjectId = "invalid-uuid";
        Project updateData = Project.builder().name("Updated Name").build();

        // When & Then
        assertThatThrownBy(() -> projectService.updateProject(ownerId, invalidProjectId, updateData))
                .isInstanceOf(IllegalArgumentException.class);
        
        verifyNoInteractions(projectRepository);
    }

    @Test
    void updateProject_ShouldUpdateOnlyProvidedFields_WhenPartialUpdate() {
        // Given
        Project existingProject = Project.builder()
                .id(projectId)
                .name("Old Name")
                .description("Old Description")
                .ownerId(ownerId)
                .build();
        
        Project updateData = Project.builder()
                .name("New Name")
                .description(null) // null description should still update
                .build();

        when(projectRepository.findByIdAndOwnerId(ownerId, projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        // When
        Project result = projectService.updateProject(ownerId, projectIdStr, updateData);

        // Then
        verify(projectRepository).save(existingProject);
        assertThat(existingProject.getName()).isEqualTo("New Name");
        assertThat(existingProject.getDescription()).isNull();
        assertThat(existingProject.getOwnerId()).isEqualTo(ownerId); // Should remain unchanged
    }

    // Helper method to create another project for testing
    private Project createAnotherProject() {
        return Project.builder()
                .id(UUID.randomUUID())
                .name("Another Project")
                .description("Another Description")
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();
    }
} 