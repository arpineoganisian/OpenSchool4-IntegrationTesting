package com.example.consumerservice.exception;

import org.springframework.validation.BindingResult;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(BindingResult result) {
        super(result.getFieldErrors().get(0).toString());
    }
}
