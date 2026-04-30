package com.dias.dteacher.service;

import com.dias.dteacher.exception.BadGatewayException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GeminiService {

    private static final String RESPONSE_MIME_TYPE = "application/json";

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public record SentencePair(String english, String portuguese) {}

    public List<SentencePair> generateSentences(String words) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadGatewayException("Chave de API do Gemini não configurada.");
        }

        String prompt = """
                Você é um professor de inglês experiente ajudando um estudante brasileiro adulto.
                Gere até 5 frases naturais em inglês utilizando as seguintes palavras ou expressões: %s.
                As frases devem ser úteis para o dia a dia.
                Responda SOMENTE com um array JSON no formato:
                [{"english": "...", "portuguese": "..."}]
                """.formatted(words.strip());

        try {
            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType(RESPONSE_MIME_TYPE)
                    .temperature(0.4f)
                    .build();
            GenerateContentResponse response = client.models.generateContent(model, prompt, config);
            return parseSentences(response.text());
        } catch (ClientException e) {
            throw handleClientException(e);
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API error: {}", e.getMessage(), e);
            throw new BadGatewayException("Serviço de IA temporariamente indisponível. Tente novamente mais tarde.", e);
        }
    }

    private BadGatewayException handleClientException(ClientException e) {
        log.warn("Gemini API client error: code={}, status={}, message={}", e.code(), e.status(), e.message());

        return switch (e.code()) {
            case 401, 403 -> new BadGatewayException("Chave de API do Gemini inválida ou sem permissão.", e);
            case 404 -> new BadGatewayException("Modelo do Gemini não encontrado: " + model + ".", e);
            case 429 -> new BadGatewayException("Quota ou limite de uso da IA excedido. Verifique o projeto no Google AI Studio ou tente novamente mais tarde.", e);
            default -> new BadGatewayException("Serviço de IA temporariamente indisponível. Tente novamente mais tarde.", e);
        };
    }

    public String translateWord(String word) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadGatewayException("Chave de API do Gemini não configurada.");
        }

        String prompt = """
                Você é um tradutor preciso inglês-português brasileiro.
                Traduza a palavra ou expressão em inglês: %s
                Responda SOMENTE com um JSON no formato: {"portuguese": "..."}
                A tradução deve ser simples, direta e natural em português brasileiro.
                """.formatted(word.strip());

        try {
            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType(RESPONSE_MIME_TYPE)
                    .temperature(0.1f)
                    .build();
            GenerateContentResponse response = client.models.generateContent(model, prompt, config);
            return parseTranslation(response.text());
        } catch (ClientException e) {
            throw handleClientException(e);
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API error on translateWord: {}", e.getMessage(), e);
            throw new BadGatewayException("Serviço de tradução temporariamente indisponível. Tente novamente mais tarde.", e);
        }
    }

    private String parseTranslation(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Resposta vazia do Gemini");
            }
            String json = text.strip();
            if (json.startsWith("```")) {
                json = json.replaceAll("```(?:json)?\\s*", "").replaceAll("```\\s*$", "").strip();
            }
            JsonNode node = objectMapper.readTree(json);
            return node.path("portuguese").asText();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar tradução do Gemini", e);
        }
    }

    private List<SentencePair> parseSentences(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Resposta vazia do Gemini");
            }

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
