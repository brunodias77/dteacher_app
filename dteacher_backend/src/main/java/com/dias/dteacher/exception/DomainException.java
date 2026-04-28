package com.dias.dteacher.exception;

import com.dias.dteacher.validation.Error;

import java.util.List;

public class DomainException extends RuntimeException {

    private final List<Error> errors;

    public DomainException(List<Error> errors) {
        super(errors.isEmpty() ? "Domain error" : errors.get(0).message());
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
