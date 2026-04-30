package com.dias.dteacher.usecase.flashcard.list;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListFlashcardsResponse(
        List<FlashcardDto> cards,
        int total,
        int totalDue
) {
    public record FlashcardDto(
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
}
