package com.crimsonlogic.open_petal_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for authorization.
 */
public class AuthorizationException extends CustomException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
