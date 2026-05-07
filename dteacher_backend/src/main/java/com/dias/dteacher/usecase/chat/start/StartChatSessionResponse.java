package com.dias.dteacher.usecase.chat.start;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StartChatSessionResponse(
        UUID sessionId,
        List<MessageDto> messages
) {
    public record MessageDto(
            UUID id,
            String sender,
            String content,
            String translation,
            OffsetDateTime createdAt
    ) {}
}
