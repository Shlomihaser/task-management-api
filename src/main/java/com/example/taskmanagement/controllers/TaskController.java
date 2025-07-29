package com.example.taskmanagement.controllers;


import com.example.taskmanagement.annotations.CurrentUser;
import com.example.taskmanagement.mappers.TaskMapper;
import com.example.taskmanagement.model.dto.requests.TaskRequestDto;
import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.dto.response.TaskResponseDto;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import com.example.taskmanagement.services.ProjectService;
import com.example.taskmanagement.services.TaskService;
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
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponseDto>> listTasks(@CurrentUser String ownerId) {

        List<Task> tasks = taskService.listTasks(ownerId);
        List<TaskResponseDto> taskResults = tasks.stream()
                .map(taskMapper::toDto)
                .toList();

        return ResponseEntity.ok(taskResults);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponseDto>> listTasksPaginated(
            @CurrentUser String ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<Task> tasks = taskService.listTasks(ownerId, pageable);
        

        List<TaskResponseDto> taskDtos = tasks.getContent().stream()
                .map(taskMapper::toDto)
                .toList();
                
        PagedResponse<TaskResponseDto> response = PagedResponse.<TaskResponseDto>builder()
                .content(taskDtos)
                .page(tasks.getPage())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all",params = "projectId")
    public ResponseEntity<List<TaskResponseDto>> listTasksByProject(@CurrentUser String ownerId,
                                                                    @RequestParam String projectId) {
        
        // 1. validate there is project with this id
        Project project = projectService.getProjectById(ownerId, projectId);

        // 2. sending to service, and he fetches all the Tasks of this project
        List<Task> tasks = taskService.listTasksByProject(project);
        
        // 3. mapping loop in order to map it to TaskResponseDto
        List<TaskResponseDto> taskResponses = tasks.stream()
                .map(taskMapper::toDto)
                .toList();
        
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping(params = "projectId")
    public ResponseEntity<PagedResponse<TaskResponseDto>> listTasksByProjectPaginated(
            @CurrentUser String ownerId,
            @RequestParam String projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        
        // Create Pageable
        Pageable pageable = PageRequest.of(page, size);
        
        PagedResponse<Task> tasks = taskService.listTasksByProject(ownerId, projectId, pageable);
        
        // Map to DTOs
        List<TaskResponseDto> taskDtos = tasks.getContent().stream()
                .map(taskMapper::toDto)
                .toList();
                
        PagedResponse<TaskResponseDto> response = PagedResponse.<TaskResponseDto>builder()
                .content(taskDtos)
                .page(tasks.getPage())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskById(@CurrentUser String ownerId,
                                                       @PathVariable String taskId) {
        // 1. get the task byId - if not the service will throw task not found
       Task task = taskService.getTaskById(ownerId, taskId);
       // 2. map it to TaskDto
       TaskResponseDto taskResponse = taskMapper.toDto(task);
        return ResponseEntity.ok(taskResponse);
    }


    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@CurrentUser String ownerId,
                                                      @Valid @RequestBody TaskRequestDto taskRequest) {
        String projectId = taskRequest.getProjectId();
        
        Task task = taskMapper.toEntity(taskRequest);
        Task createdTask = taskService.createTask(ownerId, projectId, task);
        TaskResponseDto response = taskMapper.toDto(createdTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(@CurrentUser String ownerId,
                                                @PathVariable String taskId,
                                              @Valid @RequestBody TaskRequestDto taskRequest) {
        Task taskToUpdate = taskMapper.toEntity(taskRequest);
        Task updatedTask = taskService.updateTask(ownerId, taskId, taskToUpdate);
        TaskResponseDto taskResponse = taskMapper.toDto(updatedTask);
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@CurrentUser String ownerId,
                                             @PathVariable String taskId) {
        taskService.deleteTask(ownerId, taskId);
        return ResponseEntity.ok("Task "+ taskId + " deleted successfully");
   }

}

