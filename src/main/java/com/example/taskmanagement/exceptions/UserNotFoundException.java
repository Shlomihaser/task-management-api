package com.example.taskmanagement.exceptions;

import com.example.taskmanagement.model.enums.ErrorCode;

public class UserNotFoundException extends ApiBaseException {
    public UserNotFoundException(String message) {
        super(message, ErrorCode.USER_NOT_FOUND);
    }
}
