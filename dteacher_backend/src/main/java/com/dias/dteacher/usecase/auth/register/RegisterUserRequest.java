package com.dias.dteacher.usecase.auth.register;

public record RegisterUserRequest(
        String name,
        String email,
        String password,
        String passwordConfirmation
) {
}
