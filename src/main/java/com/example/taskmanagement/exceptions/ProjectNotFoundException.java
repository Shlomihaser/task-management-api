package com.example.taskmanagement.exceptions;

import com.example.taskmanagement.model.enums.ErrorCode;

public class ProjectNotFoundException extends ApiBaseException {

    public ProjectNotFoundException() {
        super(ErrorCode.PROJECT_NOT_FOUND.getDescription(), ErrorCode.PROJECT_NOT_FOUND);
    }

    public ProjectNotFoundException(String message) {
        super(message, ErrorCode.PROJECT_NOT_FOUND);
    }
}
