package com.example.demo.exception;

import com.example.demo.validation.Errors;
import lombok.Getter;

public class ValidationException extends RuntimeException {
    @Getter
    private final Errors errors;

    public ValidationException(final Errors errors) {
        this.errors = errors;
    }
}
