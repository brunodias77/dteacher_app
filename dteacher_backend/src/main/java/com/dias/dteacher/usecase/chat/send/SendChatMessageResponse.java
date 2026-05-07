package com.dias.dteacher.usecase.chat.send;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SendChatMessageResponse(MessageDto aiMessage) {

    public record MessageDto(
            UUID id,
            String sender,
            String content,
            String translation,
            String feedback,
            OffsetDateTime createdAt
    ) {}
}
