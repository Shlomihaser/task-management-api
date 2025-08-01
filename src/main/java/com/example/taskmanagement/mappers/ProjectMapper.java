package com.example.taskmanagement.mappers;

import com.example.taskmanagement.model.dto.requests.ProjectRequestDto;
import com.example.taskmanagement.model.dto.response.ProjectResponseDto;
import com.example.taskmanagement.model.entity.Project;
import com.example.taskmanagement.model.entity.Task;
import com.example.taskmanagement.model.enums.TaskStatus;
import com.example.taskmanagement.utils.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "taskCount",source = "tasks",qualifiedByName = "calculateTaskCount")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatLocalDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "formatLocalDateTime")
    ProjectResponseDto toDto(Project project);
    Project toEntity(ProjectRequestDto project);

    @Named("calculateTaskCount")
    default long calculateTaskCount(List<Task> tasks) {
        if(tasks == null)
            return 0;

        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();
    }
    
    @Named("formatLocalDateTime")
    default String formatLocalDateTime(LocalDateTime localDateTime) {
        return DateUtils.format(localDateTime);
    }
}


