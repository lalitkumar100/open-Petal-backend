package com.crimsonlogic.open_petal_backend.exception;

import com.crimsonlogic.open_petal_backend.dto.ErrorDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug.show-stacktrace:false}")
    private boolean showStackTrace;


    private String getStackTrace(Exception e) {
        if (showStackTrace) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
        return null;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDTO> handleCustomException(CustomException ex) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .status(ex.getStatus().value())
                .stackTrace(getStackTrace(ex))
                .build();
        return new ResponseEntity<>(errorDTO, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);
        
        if (showStackTrace) {
            response.put("stackTrace", getStackTrace(ex));
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGlobalException(Exception ex) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .stackTrace(getStackTrace(ex))
                .build();
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
