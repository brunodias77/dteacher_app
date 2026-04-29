package com.dias.dteacher.usecase.generator;

import java.util.List;

public record GenerateSentencesResponse(List<Sentence> sentences) {
    public record Sentence(String english, String portuguese) {}
}
