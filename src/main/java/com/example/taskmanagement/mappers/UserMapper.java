package com.example.taskmanagement.mappers;

import com.example.taskmanagement.model.dto.UserDto;
import com.example.taskmanagement.utils.CognitoUtils;
import com.example.taskmanagement.utils.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.time.Instant;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "id", expression = "java(extractUserId(user))")
    @Mapping(target = "username", expression = "java(user.username())")
    @Mapping(target = "email", expression = "java(extractEmail(user))")
    @Mapping(target = "createdAt", expression = "java(formatDate(user.userCreateDate()))")
    @Mapping(target = "status", expression = "java(user.userStatus().toString())")
    @Mapping(target = "isEnabled", expression = "java(String.valueOf(user.enabled()))")
    UserDto toDto(UserType user);
    
    default String extractUserId(UserType user) {
        return CognitoUtils.extractAttributeValue(user, "sub");
    }
    
    default String extractEmail(UserType user) {
        return CognitoUtils.extractAttributeValue(user, "email");
    }
    
    default String formatDate(Instant instant) {
        return DateUtils.format(instant);
    }
} 