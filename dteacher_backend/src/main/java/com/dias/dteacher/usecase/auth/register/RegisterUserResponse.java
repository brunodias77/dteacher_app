package com.dias.dteacher.usecase.auth.register;

import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String email,
        String name
) {
}
