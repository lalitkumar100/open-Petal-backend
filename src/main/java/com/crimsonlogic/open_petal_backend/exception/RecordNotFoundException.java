package com.crimsonlogic.open_petal_backend.exception;

import org.springframework.http.HttpStatus;

public class RecordNotFoundException extends CustomException {
    public RecordNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
