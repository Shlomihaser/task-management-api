package com.example.taskmanagement.services;

import com.example.taskmanagement.model.dto.requests.TaskRequestDto;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import jakarta.validation.Valid;

import java.util.List;

public interface TaskService {
    List<Task> listTasks(String ownerId);
    List<Task> listTasksByProject(Project project);
    Task getTaskById(String ownerId, String taskId);
    Task updateTask(String ownerId, String taskId, Task task);
    void deleteTask(String ownerId, String taskId);
    Task createTask(String ownerId, String projectId, Task task);
}
