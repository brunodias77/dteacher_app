package com.dias.dteacher.validation;

import com.dias.dteacher.exception.DomainException;

import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {

    @Override
    public ValidationHandler append(Error error) {
        throw new DomainException(List.of(error));
    }

    @Override
    public ValidationHandler append(ValidationHandler handler) {
        throw new DomainException(handler.getErrors());
    }

    @Override
    public List<Error> getErrors() { return List.of(); }
}
