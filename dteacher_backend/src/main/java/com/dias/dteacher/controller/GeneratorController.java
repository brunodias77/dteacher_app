package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.generator.GenerateSentencesRequest;
import com.dias.dteacher.usecase.generator.GenerateSentencesUseCase;
import com.dias.dteacher.usecase.generator.TranslateWordRequest;
import com.dias.dteacher.usecase.generator.TranslateWordUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final GenerateSentencesUseCase generateSentencesUseCase;
    private final TranslateWordUseCase translateWordUseCase;

    @PostMapping("/sentences")
    public ResponseEntity<?> generate(@RequestBody GenerateSentencesRequest request) {
        return generateSentencesUseCase.execute(request).fold(
                Controllers::unprocessable,
                body -> ResponseEntity.ok(body)
        );
    }

    @PostMapping("/translate")
    public ResponseEntity<?> translate(@RequestBody TranslateWordRequest request) {
        return translateWordUseCase.execute(request).fold(
                Controllers::unprocessable,
                body -> ResponseEntity.ok(body)
        );
    }
}
