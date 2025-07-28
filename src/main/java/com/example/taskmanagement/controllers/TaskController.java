package com.example.taskmanagement.controllers;


import com.example.taskmanagement.mappers.TaskMapper;
import com.example.taskmanagement.model.dto.requests.TaskRequestDto;
import com.example.taskmanagement.model.dto.response.TaskResponseDto;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import com.example.taskmanagement.services.ProjectService;
import com.example.taskmanagement.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> listTasks(@AuthenticationPrincipal Jwt principal) {
        String ownerId = principal.getSubject();
        List<Task> tasks = taskService.listTasks(ownerId);
        List<TaskResponseDto> taskResults = tasks.stream()
                .map(taskMapper::toDto)
                .toList();

        return ResponseEntity.ok(taskResults);
    }

    @GetMapping(params = "projectId")
    public ResponseEntity<List<TaskResponseDto>> listTasksByProject(@AuthenticationPrincipal Jwt principal,
                                                                    @RequestParam String projectId) {
        String ownerId = principal.getSubject();
        
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

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskById(@AuthenticationPrincipal Jwt principal,
                                                       @PathVariable String taskId) {
        String ownerId = principal.getSubject();
        // 1. get the task byId - if not the service will throw task not found
       Task task = taskService.getTaskById(ownerId, taskId);
//       // 2. map it to TaskDto
       TaskResponseDto taskResponse = taskMapper.toDto(task);
        return ResponseEntity.ok(taskResponse);
    }


    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@AuthenticationPrincipal Jwt principal,
                                                      @Valid @RequestBody TaskRequestDto taskRequest) {
        String ownerId = principal.getSubject();
        String projectId = taskRequest.getProjectId();
        
        Task task = taskMapper.toEntity(taskRequest);
        Task createdTask = taskService.createTask(ownerId, projectId, task);
        TaskResponseDto response = taskMapper.toDto(createdTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(@AuthenticationPrincipal Jwt principal,
                                                @PathVariable String taskId,
                                              @Valid @RequestBody TaskRequestDto taskRequest) {
        String ownerId = principal.getSubject();
        Task taskToUpdate = taskMapper.toEntity(taskRequest);
        Task updatedTask = taskService.updateTask(ownerId, taskId, taskToUpdate);
        TaskResponseDto taskResponse = taskMapper.toDto(updatedTask);
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@AuthenticationPrincipal Jwt principal,
                                             @PathVariable String taskId) {
        String ownerId = principal.getSubject();
        taskService.deleteTask(ownerId, taskId);
        return ResponseEntity.ok("Task "+ taskId + " deleted successfully");
   }

}

