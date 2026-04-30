package com.dias.dteacher.usecase.flashcard.review;

import java.util.UUID;

public record ReviewFlashcardRequest(String email, UUID id, String grade) {
}
