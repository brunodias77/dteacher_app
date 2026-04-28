package com.dias.dteacher.usecase.auth.login;

public record LoginUserRequest(
        String email,
        String password
) {
}
