package com.example.taskmanagement.services;

import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    List<Task> listTasks(String ownerId);
    List<Task> listTasksByProject(Project project);

    PagedResponse<Task> listTasks(String ownerId, Pageable pageable);
    PagedResponse<Task> listTasksByProject(String ownerId, String projectId, Pageable pageable);

    Task getTaskById(String ownerId, String taskId);
    Task updateTask(String ownerId, String taskId, Task task);
    void deleteTask(String ownerId, String taskId);
    Task createTask(String ownerId, String projectId, Task task);


}
