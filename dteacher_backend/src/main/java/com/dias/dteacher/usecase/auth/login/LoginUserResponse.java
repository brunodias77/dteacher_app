package com.dias.dteacher.usecase.auth.login;

import java.util.UUID;

public record LoginUserResponse(
        String accessToken,
        long expiresIn,
        UserInfo user
) {
    public record UserInfo(UUID id, String name, String email) {}
}
