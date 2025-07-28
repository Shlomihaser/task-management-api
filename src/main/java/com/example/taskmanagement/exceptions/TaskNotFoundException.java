package com.example.taskmanagement.exceptions;

import com.example.taskmanagement.model.enums.ErrorCode;

public class TaskNotFoundException extends ApiBaseException {
    public TaskNotFoundException() {
        super(ErrorCode.TASK_NOT_FOUND.getDescription(), ErrorCode.TASK_NOT_FOUND);
    }
    public TaskNotFoundException(String message) {
        super(message, ErrorCode.TASK_NOT_FOUND);
    }
}
