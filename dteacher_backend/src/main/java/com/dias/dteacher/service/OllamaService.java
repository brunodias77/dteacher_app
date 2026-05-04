package com.dias.dteacher.service;

import com.dias.dteacher.exception.BadGatewayException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OllamaService {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:gemma3:4B-Q4_K_M}")
    private String model;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record SentencePair(String english, String portuguese) {}

    public List<SentencePair> generateSentences(String words) {
        String prompt = """
                Você é um professor de inglês experiente ajudando um estudante brasileiro adulto.
                Gere exatamente 4 frases naturais em inglês utilizando as seguintes palavras ou expressões: %s.
                As frases devem ser úteis para o dia a dia.
                Responda SOMENTE com um array JSON no formato:
                [{"english": "...", "portuguese": "..."}]
                """.formatted(words.strip());

        return parseSentences(generate(prompt, 0.4f));
    }

    public String translateWord(String word) {
        String prompt = """
                Você é um tradutor preciso inglês-português brasileiro.
                Traduza a palavra ou expressão em inglês: %s
                Responda SOMENTE com um JSON no formato: {"portuguese": "..."}
                A tradução deve ser simples, direta e natural em português brasileiro.
                """.formatted(word.strip());

        return parseTranslation(generate(prompt, 0.1f));
    }

    private String generate(String prompt, float temperature) {
        var body = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "format", "json",
                "options", Map.of("temperature", temperature)
        );

        try {
            String raw = restClient.post()
                    .uri(baseUrl + "/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode response = objectMapper.readTree(raw);

            if (response == null || !response.path("done").asBoolean()) {
                throw new BadGatewayException("Serviço de IA retornou resposta inválida.");
            }

            String text = response.path("response").asText();
            log.debug("Ollama raw response: {}", text);
            return text;
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ollama API error: {}", e.getMessage(), e);
            throw new BadGatewayException("Serviço de IA temporariamente indisponível. Tente novamente mais tarde.", e);
        }
    }

    private String parseTranslation(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Resposta vazia do modelo");
            }
            String json = stripMarkdown(text);
            JsonNode node = objectMapper.readTree(json);
            return node.path("portuguese").asText();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar tradução do modelo", e);
        }
    }

    private List<SentencePair> parseSentences(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Resposta vazia do modelo");
            }
            String json = stripMarkdown(text);
            log.debug("Parsing sentences JSON: {}", json);
            JsonNode root = objectMapper.readTree(json);

            // resolve to an array node regardless of what the model returned:
            // - bare array:              [{"english":...}, ...]
            // - single object:           {"english":..., "portuguese":...}
            // - wrapped {"sentences":[]} or {"sentences":{}}
            JsonNode arrayNode;
            if (root.isArray()) {
                arrayNode = root;
            } else if (root.has("english")) {
                arrayNode = objectMapper.createArrayNode().add(root);
            } else {
                JsonNode inner = root.path("sentences");
                arrayNode = inner.isArray() ? inner : objectMapper.createArrayNode().add(inner);
            }

            List<SentencePair> result = new ArrayList<>();
            for (JsonNode node : arrayNode) {
                String english    = node.path("english").asText();
                String portuguese = node.path("portuguese").asText();
                if (!english.isBlank() && !portuguese.isBlank()) {
                    result.add(new SentencePair(english, portuguese));
                }
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar resposta do modelo", e);
        }
    }

    private String stripMarkdown(String text) {
        String json = text.strip();
        if (json.startsWith("```")) {
            json = json.replaceAll("```(?:json)?\\s*", "").replaceAll("```\\s*$", "").strip();
        }
        return json;
    }
}
