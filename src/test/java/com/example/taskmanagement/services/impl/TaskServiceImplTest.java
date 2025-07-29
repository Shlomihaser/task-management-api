package com.example.taskmanagement.services.impl;

import com.example.taskmanagement.exceptions.TaskNotFoundException;
import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import com.example.taskmanagement.model.enums.TaskStatus;
import com.example.taskmanagement.repositories.TaskRepository;
import com.example.taskmanagement.services.ProjectService;
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
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private String ownerId;
    private String projectId;
    private UUID taskId;
    private Task task;
    private Project project;

    @BeforeEach
    void setUp() {
        ownerId = "user-123";
        taskId = UUID.randomUUID();
        UUID projectUUID = UUID.randomUUID();
        projectId = projectUUID.toString();
        
        project = Project.builder()
                .id(projectUUID)
                .name("Test Project")
                .description("Test Description")
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();

        task = Task.builder()
                .id(taskId)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Test listTasks(String ownerId)
    @Test
    void listTasks_ShouldReturnTaskList_WhenOwnerIdProvided() {
        // Given
        List<Task> expectedTasks = Arrays.asList(task, createAnotherTask());
        when(taskRepository.findAllByOwnerId(ownerId)).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.listTasks(ownerId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedTasks);
        verify(taskRepository).findAllByOwnerId(ownerId);
    }

    @Test
    void listTasks_ShouldReturnEmptyList_WhenNoTasksFound() {
        // Given
        when(taskRepository.findAllByOwnerId(ownerId)).thenReturn(List.of());

        // When
        List<Task> result = taskService.listTasks(ownerId);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository).findAllByOwnerId(ownerId);
    }

    // Test listTasks(String ownerId, Pageable pageable)
    @Test
    void listTasksPaginated_ShouldReturnPagedResponse_WhenValidInput() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = Arrays.asList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 1);
        when(taskRepository.findAllByOwnerIdPaginated(ownerId, pageable)).thenReturn(taskPage);

        // When
        PagedResponse<Task> result = taskService.listTasks(ownerId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage().getTotalElements()).isEqualTo(1);
        assertThat(result.getPage().getTotalPages()).isEqualTo(1);
        verify(taskRepository).findAllByOwnerIdPaginated(ownerId, pageable);
    }

    // Test listTasksByProject(Project project)
    @Test
    void listTasksByProject_ShouldReturnProjectTasks_WhenValidProject() {
        // Given
        List<Task> expectedTasks = Arrays.asList(task);
        when(taskRepository.findByProjectIdAndOwnerId(project.getId(), project.getOwnerId()))
                .thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.listTasksByProject(project);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(task);
        verify(taskRepository).findByProjectIdAndOwnerId(project.getId(), project.getOwnerId());
    }

    // Test listTasksByProject(String ownerId, String projectId, Pageable pageable)
    @Test
    void listTasksByProjectPaginated_ShouldReturnPagedTasks_WhenValidInput() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        List<Task> tasks = Arrays.asList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, 1);
        
        when(projectService.getProjectById(ownerId, projectId)).thenReturn(project);
        when(taskRepository.findByProjectIdAndOwnerIdPaginated(project.getId(), ownerId, pageable))
                .thenReturn(taskPage);

        // When
        PagedResponse<Task> result = taskService.listTasksByProject(ownerId, projectId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage().getTotalElements()).isEqualTo(1);
        verify(projectService).getProjectById(ownerId, projectId);
        verify(taskRepository).findByProjectIdAndOwnerIdPaginated(project.getId(), ownerId, pageable);
    }

    @Test
    void listTasksByProjectPaginated_ShouldThrowException_WhenProjectNotFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        when(projectService.getProjectById(ownerId, projectId))
                .thenThrow(new RuntimeException("Project not found"));

        // When & Then
        assertThatThrownBy(() -> taskService.listTasksByProject(ownerId, projectId, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Project not found");
        
        verify(projectService).getProjectById(ownerId, projectId);
        verifyNoInteractions(taskRepository);
    }

    // Test createTask(String ownerId, String projectId, Task task)
    @Test
    void createTask_ShouldReturnSavedTask_WhenValidInput() {
        // Given
        Task taskToCreate = Task.builder()
                .name("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .build();
        
        Task savedTask = Task.builder()
                .id(UUID.randomUUID())
                .name("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectById(ownerId, projectId)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        Task result = taskService.createTask(ownerId, projectId, taskToCreate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Task");
        assertThat(result.getProject()).isEqualTo(project);
        assertThat(taskToCreate.getProject()).isEqualTo(project); // Verify project was set
        
        verify(projectService).getProjectById(ownerId, projectId);
        verify(taskRepository).save(taskToCreate);
    }

    @Test
    void createTask_ShouldThrowException_WhenProjectNotFound() {
        // Given
        Task taskToCreate = Task.builder().name("New Task").build();
        when(projectService.getProjectById(ownerId, projectId))
                .thenThrow(new RuntimeException("Project not found"));

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(ownerId, projectId, taskToCreate))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Project not found");
        
        verify(projectService).getProjectById(ownerId, projectId);
        verifyNoInteractions(taskRepository);
    }

    // Test updateTask(String ownerId, String taskId, Task task)
    @Test
    void updateTask_ShouldReturnUpdatedTask_WhenValidInput() {
        // Given
        String taskIdStr = taskId.toString();
        Task existingTask = Task.builder()
                .id(taskId)
                .name("Old Name")
                .description("Old Description")
                .status(TaskStatus.TODO)
                .project(project)
                .build();
        
        Task updateData = Task.builder()
                .name("Updated Name")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .build();
        
        Task updatedTask = Task.builder()
                .id(taskId)
                .name("Updated Name")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .project(project)
                .build();

        when(taskRepository.findByIdAndOwnerId(taskId, ownerId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        Task result = taskService.updateTask(ownerId, taskIdStr, updateData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        
        verify(taskRepository).findByIdAndOwnerId(taskId, ownerId);
        verify(taskRepository).save(existingTask);
        
        // Verify the existing task was updated
        assertThat(existingTask.getName()).isEqualTo("Updated Name");
        assertThat(existingTask.getDescription()).isEqualTo("Updated Description");
        assertThat(existingTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateTask_ShouldThrowTaskNotFoundException_WhenTaskNotFound() {
        // Given
        String taskIdStr = taskId.toString();
        Task updateData = Task.builder().name("Updated Name").build();
        
        when(taskRepository.findByIdAndOwnerId(taskId, ownerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(ownerId, taskIdStr, updateData))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: " + taskIdStr);
        
        verify(taskRepository).findByIdAndOwnerId(taskId, ownerId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_ShouldThrowIllegalArgumentException_WhenInvalidTaskId() {
        // Given
        String invalidTaskId = "invalid-uuid";
        Task updateData = Task.builder().name("Updated Name").build();

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(ownerId, invalidTaskId, updateData))
                .isInstanceOf(IllegalArgumentException.class);
        
        verifyNoInteractions(taskRepository);
    }

    // Test deleteTask(String ownerId, String taskId)
    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExists() {
        // Given
        String taskIdStr = taskId.toString();
        when(taskRepository.findByIdAndOwnerId(taskId, ownerId)).thenReturn(Optional.of(task));

        // When
        taskService.deleteTask(ownerId, taskIdStr);

        // Then
        verify(taskRepository).findByIdAndOwnerId(taskId, ownerId);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_ShouldThrowTaskNotFoundException_WhenTaskNotFound() {
        // Given
        String taskIdStr = taskId.toString();
        when(taskRepository.findByIdAndOwnerId(taskId, ownerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(ownerId, taskIdStr))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: " + taskIdStr);
        
        verify(taskRepository).findByIdAndOwnerId(taskId, ownerId);
        verify(taskRepository, never()).delete(any());
    }

    // Test getTaskById(String ownerId, String taskId)
    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExists() {
        // Given
        String taskIdStr = taskId.toString();
        when(taskRepository.findByIdAndOwnerId(taskId, ownerId)).thenReturn(Optional.of(task));

        // When
        Task result = taskService.getTaskById(ownerId, taskIdStr);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(task);
        verify(taskRepository).findByIdAndOwnerId(taskId, ownerId);
    }

    @Test
    void getTaskById_ShouldThrowTaskNotFoundException_WhenTaskNotFound() {
        // Given
        String taskIdStr = taskId.toString();
        when(taskRepository.findByIdAndOwnerId(taskId, ownerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(ownerId, taskIdStr))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: " + taskIdStr);
        
        verify(taskRepository).findByIdAndOwnerId(taskId, ownerId);
    }

    @Test
    void getTaskById_ShouldThrowIllegalArgumentException_WhenInvalidTaskId() {
        // Given
        String invalidTaskId = "invalid-uuid";

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(ownerId, invalidTaskId))
                .isInstanceOf(IllegalArgumentException.class);
        
        verifyNoInteractions(taskRepository);
    }

    // Helper method to create another task for testing
    private Task createAnotherTask() {
        return Task.builder()
                .id(UUID.randomUUID())
                .name("Another Task")
                .description("Another Description")
                .status(TaskStatus.IN_PROGRESS)
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();
    }
} 