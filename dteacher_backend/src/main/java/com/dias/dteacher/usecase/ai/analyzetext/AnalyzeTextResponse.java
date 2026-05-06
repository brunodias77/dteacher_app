package com.dias.dteacher.usecase.ai.analyzetext;

import java.util.List;

public record AnalyzeTextResponse(
        String overview,
        List<SentenceAnalysis> analysis,
        List<AnkiCard> ankiCards
) {
    public record SentenceAnalysis(String sentence, String translation, String notes) {}
    public record AnkiCard(String english, String portuguese) {}
}
