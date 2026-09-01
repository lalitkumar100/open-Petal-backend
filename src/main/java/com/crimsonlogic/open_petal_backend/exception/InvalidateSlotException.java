package com.crimsonlogic.open_petal_backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidateSlotException extends  CustomException{
    public InvalidateSlotException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
