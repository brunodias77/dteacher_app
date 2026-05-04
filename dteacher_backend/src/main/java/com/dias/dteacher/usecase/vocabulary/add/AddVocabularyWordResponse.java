package com.dias.dteacher.usecase.vocabulary.add;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AddVocabularyWordResponse(
        UUID id,
        String word,
        String translation,
        String example,
        String exampleTranslation,
        OffsetDateTime createdAt
) {}
