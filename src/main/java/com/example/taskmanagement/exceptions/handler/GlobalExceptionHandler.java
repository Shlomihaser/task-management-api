package com.example.taskmanagement.exceptions.handler;


import com.example.taskmanagement.exceptions.ApiBaseException;
import com.example.taskmanagement.exceptions.AuthenticationException;
import com.example.taskmanagement.model.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ResponseEntity<ErrorDto> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {


        List<ErrorDto.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDto.FieldError(
                        error.getField(),           // Field name
                        error.getDefaultMessage()   // Error message
                ))
                .toList();

        log.warn("Validation failed for request: {} {}. Errors: {}", 
                request.getMethod(), request.getRequestURI(), 
                fieldErrors.stream().map(fe -> fe.field() + ": " + fe.message()).collect(Collectors.joining(", ")));


        ErrorDto response = new ErrorDto(
                "Validation failed",
                "VALIDATION_ERROR",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        String message = String.format("HTTP method '%s' not supported for this endpoint. Supported methods: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());
        
        log.warn("Method not allowed for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), message);
        
        ErrorDto response = new ErrorDto(
                message,
                "METHOD_NOT_ALLOWED",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        
        String message = String.format("Media type '%s' not supported. Supported types: %s",
                ex.getContentType(), ex.getSupportedMediaTypes());
        
        log.warn("Unsupported media type for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), message);
        
        ErrorDto response = new ErrorDto(
                message,
                "UNSUPPORTED_MEDIA_TYPE",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        
        log.warn("Missing parameter for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), message);
        
        ErrorDto response = new ErrorDto(
                message,
                "MISSING_REQUIRED_FIELD",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleInvalidJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        String message = "Invalid JSON format or data type mismatch";
        
        log.warn("Invalid JSON for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        
        ErrorDto response = new ErrorDto(
                message,
                "INVALID_REQUEST_FORMAT",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorDto> handleInvalidPathVariable(
            Exception ex, HttpServletRequest request) {
        
        String message = "Invalid parameter format";
        
        log.warn("Invalid parameter format for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        
        ErrorDto response = new ErrorDto(
                message,
                "INVALID_UUID_FORMAT",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        

        log.warn("Authentication failed for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorDto response = new ErrorDto(
                ex.getMessage(),
                ex.getErrorCode().name(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(ApiBaseException.class)
    public ResponseEntity<ErrorDto> handleApiBaseException(
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

        ErrorDto response = new ErrorDto(
                ex.getMessage(),
                ex.getErrorCode().name(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(CognitoIdentityProviderException.class)
    public ResponseEntity<ErrorDto> handleCognitoException(
            CognitoIdentityProviderException ex, HttpServletRequest request) {
        
        String cognitoErrorMessage = ex.awsErrorDetails().errorMessage();
        

        log.error("Cognito service error for request: {} {}. AWS Error Code: {}, Message: {}", 
                request.getMethod(), request.getRequestURI(), 
                ex.awsErrorDetails().errorCode(), cognitoErrorMessage);

        ErrorDto response = new ErrorDto(
                cognitoErrorMessage,
                "EXTERNAL_SERVICE_ERROR",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        

        log.warn("Invalid argument for request: {} {}. Error: {}", 
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorDto response = new ErrorDto(
                ex.getMessage(),
                "INVALID_REQUEST_FORMAT",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        log.error("Data integrity violation for request: {} {}. Error: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ErrorDto response = new ErrorDto(
                "Data integrity violation: " + ex.getMessage(),
                "RESOURCE_CONFLICT",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleOtherExceptions(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error occurred for request: {} {}. Error: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ErrorDto response = new ErrorDto(
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
