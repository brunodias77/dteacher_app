package com.dias.dteacher.usecase.flashcard.review;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewFlashcardResponse(
        UUID id,
        String english,
        String portuguese,
        String source,
        OffsetDateTime nextReview,
        Integer intervalDays,
        BigDecimal easeFactor,
        Integer repetitions,
        OffsetDateTime createdAt
) {}
