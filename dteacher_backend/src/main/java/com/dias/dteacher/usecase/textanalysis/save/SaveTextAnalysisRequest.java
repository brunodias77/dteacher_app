package com.dias.dteacher.usecase.textanalysis.save;

public record SaveTextAnalysisRequest(
        String email,
        String originalText,
        String overview,
        String analysisData,
        String ankiCards
) {}
