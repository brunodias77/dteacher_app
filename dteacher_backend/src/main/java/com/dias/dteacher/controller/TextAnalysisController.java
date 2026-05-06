package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.textanalysis.save.SaveTextAnalysisRequest;
import com.dias.dteacher.usecase.textanalysis.save.SaveTextAnalysisUseCase;
import com.fasterxml.jackson.databind.JsonNode;
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
@RequestMapping("/api/text-analyses")
@RequiredArgsConstructor
public class TextAnalysisController {

    private final SaveTextAnalysisUseCase saveTextAnalysisUseCase;

    @PostMapping
    public ResponseEntity<?> save(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SaveBody body) {

        var request = new SaveTextAnalysisRequest(
                userDetails.getUsername(),
                body.originalText(),
                body.overview(),
                body.analysisData() != null ? body.analysisData().toString() : "[]",
                body.ankiCards()    != null ? body.ankiCards().toString()    : "[]"
        );

        return saveTextAnalysisUseCase.execute(request).fold(
                Controllers::unprocessable,
                resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp)
        );
    }

    record SaveBody(
            String originalText,
            String overview,
            JsonNode analysisData,
            JsonNode ankiCards
    ) {}
}
