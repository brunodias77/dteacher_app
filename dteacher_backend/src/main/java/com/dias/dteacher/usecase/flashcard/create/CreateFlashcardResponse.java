package com.dias.dteacher.usecase.flashcard.create;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateFlashcardResponse(
        UUID id,
        String english,
        String portuguese,
        String source,
        OffsetDateTime nextReview,
        Integer intervalDays,
        BigDecimal easeFactor,
        Integer repetitions,
        OffsetDateTime createdAt
) {
}
