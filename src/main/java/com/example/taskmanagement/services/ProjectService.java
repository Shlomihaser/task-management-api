package com.example.taskmanagement.services;

import com.example.taskmanagement.model.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<Project> listProjects(String ownerId);
    Project createProject(String ownerId, Project project);
    Project getProjectById(String ownerId,String projectId);
    void deleteProject(String ownerId,String projectId);
    Project updateProject(String ownerId, String projectId, Project project);
}
