package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.ai.analyzetext.AnalyzeTextRequest;
import com.dias.dteacher.usecase.ai.analyzetext.AnalyzeTextUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AnalyzeTextUseCase analyzeTextUseCase;

    @PostMapping("/analyze-text")
    public ResponseEntity<?> analyzeText(@RequestBody AnalyzeTextBody body) {
        return analyzeTextUseCase.execute(new AnalyzeTextRequest(body.text())).fold(
                Controllers::unprocessable,
                body2 -> ResponseEntity.ok(body2)
        );
    }

    record AnalyzeTextBody(String text) {}
}
