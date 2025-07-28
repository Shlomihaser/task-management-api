package com.example.taskmanagement.exceptions.handler;


import com.example.taskmanagement.exceptions.ApiBaseException;
import com.example.taskmanagement.exceptions.AuthenticationException;
import com.example.taskmanagement.model.dto.response.ErrorResponse;
import com.example.taskmanagement.model.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {


        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),           // Field name
                        error.getDefaultMessage()   // Error message
                ))
                .toList();

        log.warn("Validation failed for request: {} {}. Errors: {}", 
                request.getMethod(), request.getRequestURI(), 
                fieldErrors.stream().map(fe -> fe.field() + ": " + fe.message()).collect(Collectors.joining(", ")));


        ErrorResponse response = new ErrorResponse(
                "Validation failed",
                "VALIDATION_ERROR",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        String message = String.format("HTTP method '%s' not supported for this endpoint. Supported methods: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());
        
        log.warn("Method not allowed for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), message);
        
        ErrorResponse response = new ErrorResponse(
                message,
                "METHOD_NOT_ALLOWED",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        
        String message = String.format("Media type '%s' not supported. Supported types: %s",
                ex.getContentType(), ex.getSupportedMediaTypes());
        
        log.warn("Unsupported media type for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), message);
        
        ErrorResponse response = new ErrorResponse(
                message,
                "UNSUPPORTED_MEDIA_TYPE",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        
        log.warn("Missing parameter for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), message);
        
        ErrorResponse response = new ErrorResponse(
                message,
                "MISSING_REQUIRED_FIELD",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        String message = "Invalid JSON format or data type mismatch";
        
        log.warn("Invalid JSON for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(
                message,
                "INVALID_REQUEST_FORMAT",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleInvalidPathVariable(
            Exception ex, HttpServletRequest request) {
        
        String message = "Invalid parameter format";
        
        log.warn("Invalid parameter format for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(
                message,
                "INVALID_UUID_FORMAT",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        

        log.warn("Authentication failed for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode().name(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(ApiBaseException.class)
    public ResponseEntity<ErrorResponse> handleApiBaseException(
            ApiBaseException ex, HttpServletRequest request) {
        

        HttpStatus status = ex.getErrorCode().getHttpStatus();
        if (status.is4xxClientError()) {
            log.warn("Client error for request: {} {}. Error: {} ({})", 
                    request.getMethod(), request.getRequestURI(), 
                    ex.getMessage(), ex.getErrorCode());
        } else {
            log.error("Server error for request: {} {}. Error: {} ({})", 
                    request.getMethod(), request.getRequestURI(), 
                    ex.getMessage(), ex.getErrorCode(), ex);
        }

        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode().name(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(CognitoIdentityProviderException.class)
    public ResponseEntity<ErrorResponse> handleCognitoException(
            CognitoIdentityProviderException ex, HttpServletRequest request) {
        
        String cognitoErrorMessage = ex.awsErrorDetails().errorMessage();
        

        log.error("Cognito service error for request: {} {}. AWS Error Code: {}, Message: {}", 
                request.getMethod(), request.getRequestURI(), 
                ex.awsErrorDetails().errorCode(), cognitoErrorMessage);

        ErrorResponse response = new ErrorResponse(
                cognitoErrorMessage,
                "EXTERNAL_SERVICE_ERROR",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        

        log.warn("Invalid argument for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                "INVALID_REQUEST_FORMAT",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        log.error("Data integrity violation for request: {} {}. Error: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
                "Data integrity violation: " + ex.getMessage(),
                "RESOURCE_CONFLICT",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error occurred for request: {} {}. Error: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
