package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.flashcard.create.CreateFlashcardRequest;
import com.dias.dteacher.usecase.flashcard.create.CreateFlashcardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final CreateFlashcardUseCase createFlashcardUseCase;

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateFlashcardRequest body) {
        var request = new CreateFlashcardRequest(
                userDetails.getUsername(),
                body.english(),
                body.portuguese(),
                body.source()
        );
        return createFlashcardUseCase.execute(request).fold(
                n    -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(n),
                body2 -> ResponseEntity.status(HttpStatus.CREATED).body(body2)
        );
    }
}
