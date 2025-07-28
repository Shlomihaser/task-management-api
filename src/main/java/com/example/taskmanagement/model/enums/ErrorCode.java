package com.example.taskmanagement.model.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    // 400 - Bad Request
    VALIDATION_ERROR("Validation failed", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_FORMAT("Invalid request format", HttpStatus.BAD_REQUEST),
    INVALID_UUID_FORMAT("Invalid UUID format", HttpStatus.BAD_REQUEST),
    INVALID_TASK_STATUS("Invalid task status", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("Missing required field", HttpStatus.BAD_REQUEST),
    
    // 401 - Unauthorized  
    AUTHENTICATION_FAILED("Authentication failed", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid or expired token", HttpStatus.UNAUTHORIZED),
    PASSWORD_CHANGE_REQUIRED("Password change required", HttpStatus.UNAUTHORIZED),
    
    // 403 - Forbidden
    FORBIDDEN("Access denied", HttpStatus.FORBIDDEN),
    PROJECT_ACCESS_DENIED("Access denied to this project", HttpStatus.FORBIDDEN),
    TASK_ACCESS_DENIED("Access denied to this task", HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSIONS("Insufficient permissions", HttpStatus.FORBIDDEN),
    
    // 404 - Not Found
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    PROJECT_NOT_FOUND("Project not found", HttpStatus.NOT_FOUND),
    TASK_NOT_FOUND("Task not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    
    // 405 - Method Not Allowed
    METHOD_NOT_ALLOWED("HTTP method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    
    // 409 - Conflict
    RESOURCE_CONFLICT("Resource conflict", HttpStatus.CONFLICT),
    PROJECT_ALREADY_EXISTS("Project already exists", HttpStatus.CONFLICT),
    TASK_ALREADY_EXISTS("Task already exists", HttpStatus.CONFLICT),
    PROJECT_HAS_TASKS("Cannot delete project with existing tasks", HttpStatus.CONFLICT),
    
    // 415 - Unsupported Media Type
    UNSUPPORTED_MEDIA_TYPE("Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    
    // 429 - Too Many Requests
    TOO_MANY_REQUESTS("Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    
    // 500 - Internal Server Error
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("Database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("External service error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String description;
    private final HttpStatus httpStatus;

    ErrorCode(String description, HttpStatus httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
