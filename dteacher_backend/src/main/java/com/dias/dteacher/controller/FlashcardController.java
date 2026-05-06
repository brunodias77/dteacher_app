package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.flashcard.create.CreateFlashcardRequest;
import com.dias.dteacher.usecase.flashcard.create.CreateFlashcardUseCase;
import com.dias.dteacher.usecase.flashcard.list.ListFlashcardsRequest;
import com.dias.dteacher.usecase.flashcard.list.ListFlashcardsUseCase;
import com.dias.dteacher.usecase.flashcard.review.ReviewFlashcardRequest;
import com.dias.dteacher.usecase.flashcard.review.ReviewFlashcardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final CreateFlashcardUseCase createFlashcardUseCase;
    private final ListFlashcardsUseCase  listFlashcardsUseCase;
    private final ReviewFlashcardUseCase reviewFlashcardUseCase;

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal UserDetails userDetails) {
        return listFlashcardsUseCase.execute(new ListFlashcardsRequest(userDetails.getUsername()))
                .fold(
                        Controllers::unprocessable,
                        body -> ResponseEntity.ok(body)
                );
    }

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
                Controllers::unprocessable,
                body2 -> ResponseEntity.status(HttpStatus.CREATED).body(body2)
        );
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<?> review(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody ReviewBody body) {
        var request = new ReviewFlashcardRequest(userDetails.getUsername(), id, body.grade());
        return reviewFlashcardUseCase.execute(request).fold(
                Controllers::unprocessable,
                resp -> ResponseEntity.ok(resp)
        );
    }

    record ReviewBody(String grade) {}
}
