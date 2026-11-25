package com.crimsonlogic.open_petal_backend.common.exception;

import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageService messageService;

    public GlobalExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), messageService.getMessage(MessageCodes.COMMON_VALIDATION_FAILED)));
        return response(HttpStatus.BAD_REQUEST, MessageCodes.COMMON_VALIDATION_FAILED, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errors.putIfAbsent(violation.getPropertyPath().toString(),
                        messageService.getMessage(MessageCodes.COMMON_VALIDATION_FAILED)));
        return response(HttpStatus.BAD_REQUEST, MessageCodes.COMMON_VALIDATION_FAILED, errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException exception) {
        return failure(HttpStatus.NOT_FOUND, MessageCodes.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException exception) {
        return failure(HttpStatus.NOT_FOUND, MessageCodes.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException exception) {
        return failure(HttpStatus.CONFLICT, MessageCodes.DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        return failure(HttpStatus.UNAUTHORIZED, MessageCodes.AUTH_INVALID_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException exception) {
        return failure(HttpStatus.FORBIDDEN, MessageCodes.ACCESS_DENIED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException exception) {
        return failure(HttpStatus.UNAUTHORIZED, MessageCodes.AUTHENTICATION_REQUIRED);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorization(AuthorizationException exception) {
        return failure(HttpStatus.FORBIDDEN, MessageCodes.ACCESS_DENIED);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException exception) {
        return failure(HttpStatus.BAD_REQUEST, MessageCodes.COMMON_BUSINESS_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return failure(HttpStatus.BAD_REQUEST, MessageCodes.COMMON_BUSINESS_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustom(CustomException exception) {
        HttpStatus status = exception.getStatus();
        String code = status == HttpStatus.NOT_FOUND
                ? MessageCodes.RESOURCE_NOT_FOUND
                : status == HttpStatus.CONFLICT
                        ? MessageCodes.DUPLICATE_RESOURCE
                        : MessageCodes.COMMON_BUSINESS_ERROR;
        return failure(status, code);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        return failure(HttpStatus.CONFLICT, MessageCodes.COMMON_DATABASE_CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException exception) {
        return failure(HttpStatus.BAD_REQUEST, MessageCodes.COMMON_BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequestParameter(Exception exception) {
        return failure(HttpStatus.BAD_REQUEST, MessageCodes.COMMON_BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception) {
        return failure(HttpStatus.METHOD_NOT_ALLOWED, MessageCodes.COMMON_METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
        log.error("Unhandled exception while processing request", exception);
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, MessageCodes.COMMON_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> failure(HttpStatus status, String code) {
        return response(status, code, null);
    }

    private <T> ResponseEntity<ApiResponse<T>> response(HttpStatus status, String code, T data) {
        ApiResponse<T> body = ApiResponse.failure(code, messageService.getMessage(code), data);
        return ResponseEntity.status(status).body(body);
    }
}
