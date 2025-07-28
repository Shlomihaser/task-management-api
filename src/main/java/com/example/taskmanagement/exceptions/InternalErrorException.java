package com.example.taskmanagement.exceptions;

import com.example.taskmanagement.model.enums.ErrorCode;

public class InternalErrorException extends ApiBaseException {
    public InternalErrorException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR);
    }
    public InternalErrorException(String message,Exception e) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR,e);
    }

}
