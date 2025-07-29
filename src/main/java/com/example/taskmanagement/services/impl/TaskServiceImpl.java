package com.example.taskmanagement.services.impl;

import com.example.taskmanagement.exceptions.TaskNotFoundException;
import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import com.example.taskmanagement.repositories.TaskRepository;
import com.example.taskmanagement.services.ProjectService;
import com.example.taskmanagement.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Override
    public List<Task> listTasks(String ownerId) {
        return taskRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public PagedResponse<Task> listTasks(String ownerId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAllByOwnerIdPaginated(ownerId, pageable);
        return PagedResponse.of(taskPage);
    }

    @Override
    public List<Task> listTasksByProject(Project project) {
        return taskRepository.findByProjectIdAndOwnerId(project.getId(), project.getOwnerId());
    }

    @Override
    public PagedResponse<Task> listTasksByProject(String ownerId, String projectId, Pageable pageable) {
        // Validate project exists and belongs to user
        Project project = projectService.getProjectById(ownerId, projectId);
        
        Page<Task> taskPage = taskRepository.findByProjectIdAndOwnerIdPaginated(
            project.getId(), ownerId, pageable);
        return PagedResponse.of(taskPage);
    }

    @Override
    public Task createTask(String ownerId, String projectId, Task task) {
        // 1. Validate project exists and belongs to user
        Project project = projectService.getProjectById(ownerId, projectId);
        task.setProject(project);

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(String ownerId, String taskId, Task task){

        Optional<Task> existingTask = Optional.ofNullable(getTaskById(ownerId, taskId));
        if (existingTask.isEmpty())
            throw new TaskNotFoundException("Task not found with id: " + taskId);

        Task existing = existingTask.get();
        existing.setName(task.getName());
        existing.setDescription(task.getDescription());
        existing.setStatus(task.getStatus());
        return taskRepository.save(existing);
    }

    @Override
    public void deleteTask(String ownerId, String taskId) {
        Task task = getTaskById(ownerId, taskId);
        taskRepository.delete(task);
    }

    @Override
    public Task getTaskById(String ownerId, String taskId) {
        UUID taskUuid = UUID.fromString(taskId);
        Optional<Task> task = taskRepository.findByIdAndOwnerId(taskUuid, ownerId);
        if (task.isEmpty())
            throw new TaskNotFoundException("Task not found with id: " + taskId);

        return task.get();
    }

}
