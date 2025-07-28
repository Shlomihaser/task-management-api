package com.example.taskmanagement.exceptions;

import com.example.taskmanagement.model.enums.ErrorCode;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;


public class AuthenticationException extends ApiBaseException {

    public AuthenticationException() {
        super(ErrorCode.AUTHENTICATION_FAILED.getDescription(), ErrorCode.AUTHENTICATION_FAILED);
    }

    public AuthenticationException(String message) {
        super(message, ErrorCode.AUTHENTICATION_FAILED);
    }


    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
