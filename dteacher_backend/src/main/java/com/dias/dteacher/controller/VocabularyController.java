package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.vocabulary.add.AddVocabularyWordRequest;
import com.dias.dteacher.usecase.vocabulary.add.AddVocabularyWordUseCase;
import com.dias.dteacher.usecase.vocabulary.list.ListVocabularyWordsRequest;
import com.dias.dteacher.usecase.vocabulary.list.ListVocabularyWordsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vocabulary")
@RequiredArgsConstructor
public class VocabularyController {

    private final AddVocabularyWordUseCase    addVocabularyWordUseCase;
    private final ListVocabularyWordsUseCase  listVocabularyWordsUseCase;

    @GetMapping
    public ResponseEntity<?> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String q) {
        return listVocabularyWordsUseCase
                .execute(new ListVocabularyWordsRequest(userDetails.getUsername(), q))
                .fold(
                        Controllers::unprocessable,
                        body -> ResponseEntity.ok(body)
                );
    }

    @PostMapping
    public ResponseEntity<?> add(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddVocabularyWordBody body) {
        var request = new AddVocabularyWordRequest(userDetails.getUsername(), body.word());
        return addVocabularyWordUseCase.execute(request).fold(
                Controllers::unprocessable,
                response -> ResponseEntity.status(HttpStatus.CREATED).body(response)
        );
    }

    record AddVocabularyWordBody(String word) {}
}
