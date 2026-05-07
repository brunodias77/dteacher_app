package com.dias.dteacher.usecase.chat.start;

public record StartChatSessionRequest(
        String email,
        String mode,
        String cefrLevel
) {}
