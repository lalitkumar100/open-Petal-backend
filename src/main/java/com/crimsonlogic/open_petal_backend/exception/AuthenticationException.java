package com.crimsonlogic.open_petal_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for authentication.
 */
public class AuthenticationException extends CustomException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
