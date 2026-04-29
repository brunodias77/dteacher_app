package com.dias.dteacher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash-preview-09-2025}")
    private String model;

    private final RestClient   restClient   = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record SentencePair(String english, String portuguese) {}

    public List<SentencePair> generateSentences(String words) {
        String prompt = """
                Você é um professor de inglês experiente ajudando um estudante brasileiro adulto.
                Gere até 5 frases naturais em inglês utilizando as seguintes palavras ou expressões: %s.
                As frases devem ser úteis para o dia a dia.
                """.formatted(words.strip());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "ARRAY",
                                "items", Map.of(
                                        "type", "OBJECT",
                                        "properties", Map.of(
                                                "english",    Map.of("type", "STRING"),
                                                "portuguese", Map.of("type", "STRING")
                                        ),
                                        "required", List.of("english", "portuguese")
                                )
                        )
                )
        );

        String url = BASE_URL.formatted(model, apiKey);

        String responseBody = restClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return parseSentences(responseBody);
    }

    private List<SentencePair> parseSentences(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            JsonNode sentences = objectMapper.readTree(text);
            List<SentencePair> result = new ArrayList<>();
            for (JsonNode node : sentences) {
                result.add(new SentencePair(
                        node.path("english").asText(),
                        node.path("portuguese").asText()
                ));
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar resposta do Gemini", e);
        }
    }
}
