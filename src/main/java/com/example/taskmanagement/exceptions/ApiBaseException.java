package com.example.taskmanagement.exceptions;

import com.example.taskmanagement.model.enums.ErrorCode;
import lombok.Getter;


@Getter
public class ApiBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiBaseException(String message, ErrorCode errorCode,Exception e) {
        super(message,e);
        this.errorCode = errorCode;
    }


}