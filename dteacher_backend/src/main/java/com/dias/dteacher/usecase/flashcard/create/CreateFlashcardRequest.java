package com.dias.dteacher.usecase.flashcard.create;

public record CreateFlashcardRequest(
        String email,
        String english,
        String portuguese,
        String source
) {
}
