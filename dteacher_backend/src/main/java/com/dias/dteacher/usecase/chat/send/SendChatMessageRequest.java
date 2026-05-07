package com.dias.dteacher.usecase.chat.send;

import java.util.UUID;

public record SendChatMessageRequest(
        String email,
        UUID sessionId,
        String content
) {}
