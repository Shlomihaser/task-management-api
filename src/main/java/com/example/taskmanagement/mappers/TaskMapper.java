package com.example.taskmanagement.mappers;

import com.example.taskmanagement.model.dto.requests.TaskRequestDto;
import com.example.taskmanagement.model.dto.response.TaskResponseDto;
import com.example.taskmanagement.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    TaskResponseDto toDto(Task task);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskRequestDto taskRequestDto);

} 