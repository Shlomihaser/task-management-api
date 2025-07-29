package com.example.taskmanagement.services.impl;


import com.example.taskmanagement.exceptions.ProjectNotFoundException;
import com.example.taskmanagement.model.dto.response.PagedResponse;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.repositories.ProjectRepository;
import com.example.taskmanagement.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public List<Project> listProjects(String ownerId) {
        return projectRepository.findAllWithTasks(ownerId);
    }

    @Override
    public PagedResponse<Project> listProjects(String ownerId, Pageable pageable) {
        Page<Project> projectPage = projectRepository.findAllWithTasksPaginated(ownerId, pageable);
        return PagedResponse.of(projectPage);
    }

    @Override
    public Project createProject(String ownerId, Project project) {
        project.setOwnerId(ownerId);
        return projectRepository.save(project);
    }

    @Override
    public Project getProjectById(String ownerId, String projectId) {
        UUID projectUuid = UUID.fromString(projectId);

        Optional<Project> project = projectRepository.findByIdAndOwnerId(ownerId, projectUuid);
        if (project.isEmpty())
            throw new ProjectNotFoundException("Project not found with id: " + projectId);


        return project.get();
    }

    @Override
    public void deleteProject(String ownerId, String projectId) {
        UUID projectUuid = UUID.fromString(projectId);

        // Check if project exists and belongs to user
        Optional<Project> projectOpt = projectRepository.findByIdAndOwnerId(ownerId, projectUuid);
        if (projectOpt.isEmpty())
            throw new ProjectNotFoundException("Project not found with id: " + projectId);

        Project project = projectOpt.get();
        
        // Delete project and all associated tasks (cascade delete configured in Project entity)
        // This will automatically delete all tasks that belong to this project
        projectRepository.delete(project);
    }


    @Override
    public Project updateProject(String ownerId, String projectId, Project project) {
        UUID projectUuid = UUID.fromString(projectId);

        Optional<Project> existingProject = projectRepository.findByIdAndOwnerId(ownerId, projectUuid);
        if (existingProject.isEmpty())
            throw new ProjectNotFoundException("Project not found with id: " + projectId);

        Project existing = existingProject.get();
        existing.setName(project.getName());
        existing.setDescription(project.getDescription());

        return projectRepository.save(existing);
    }

}
