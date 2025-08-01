package com.example.taskmanagement.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDto(
        String message,
        String errorCode,
        long timestamp,
        String path,
        List<FieldError> fieldErrors
) {
    // Constructor for simple errors (no field validation errors)
    public ErrorDto(String message, String errorCode, String path) {
        this(message, errorCode, System.currentTimeMillis(), path, null);
    }
    
    // Constructor for validation errors with field details
    public ErrorDto(String message, String errorCode, String path, List<FieldError> fieldErrors) {
        this(message, errorCode, System.currentTimeMillis(), path, fieldErrors);
    }
    
    // Nested record for field-level validation errors
    public record FieldError(String field, String message) {}
}
