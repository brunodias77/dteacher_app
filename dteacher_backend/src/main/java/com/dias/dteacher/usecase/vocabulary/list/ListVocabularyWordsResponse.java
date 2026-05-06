package com.dias.dteacher.usecase.vocabulary.list;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListVocabularyWordsResponse(List<WordDto> words, int total) {

    public record WordDto(
            UUID id,
            String word,
            String translation,
            String example,
            String exampleTranslation,
            OffsetDateTime createdAt
    ) {}
}
