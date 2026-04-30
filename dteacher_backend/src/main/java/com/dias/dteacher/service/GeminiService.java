package com.dias.dteacher.service;

import com.dias.dteacher.exception.BadGatewayException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public record SentencePair(String english, String portuguese) {}

    public List<SentencePair> generateSentences(String words) {
        String prompt = """
                Você é um professor de inglês experiente ajudando um estudante brasileiro adulto.
                Gere até 5 frases naturais em inglês utilizando as seguintes palavras ou expressões: %s.
                As frases devem ser úteis para o dia a dia.
                Responda SOMENTE com um array JSON no formato:
                [{"english": "...", "portuguese": "..."}]
                """.formatted(words.strip());

        try {
            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentResponse response = client.models.generateContent(model, prompt, null);
            return parseSentences(response.text());
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API error: {}", e.getMessage(), e);
            throw new BadGatewayException("Serviço de IA temporariamente indisponível. Tente novamente mais tarde.", e);
        }
    }

    private List<SentencePair> parseSentences(String text) {
        try {
            String json = text.strip();
            if (json.startsWith("```")) {
                json = json.replaceAll("```(?:json)?\\s*", "").replaceAll("```\\s*$", "").strip();
            }
            JsonNode sentences = objectMapper.readTree(json);
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
