package com.example.taskmanagement.services;

import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.entity.Project;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProjectService {

    List<Project> listProjects(String ownerId);
    PagedResponse<Project> listProjects(String ownerId, Pageable pageable);

    Project getProjectById(String ownerId, String projectId);

    Project createProject(String ownerId, Project project);
    void deleteProject(String ownerId, String projectId);
    Project updateProject(String ownerId, String projectId, Project project);

}
