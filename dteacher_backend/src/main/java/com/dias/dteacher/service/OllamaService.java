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

    public record WordEnrichment(String word, String translation, String example, String exampleTranslation) {}

    public record SentenceAnalysis(String sentence, String translation, String notes) {}

    public record AnkiCard(String english, String portuguese) {}

    public record TextAnalysisResult(String overview, List<SentenceAnalysis> analysis, List<AnkiCard> ankiCards) {}

    public record ChatTurn(String sender, String content) {}

    public record ChatResponse(String text, String translation, String feedback) {}

    public List<SentencePair> generateSentences(String words) {
        String prompt = """
                Você é um professor de inglês experiente ajudando um estudante brasileiro adulto.
                Gere exatamente 4 frases naturais em inglês utilizando as seguintes palavras ou expressões: %s.
                As frases devem ser úteis para o dia a dia.
                Responda SOMENTE com um objeto JSON no formato:
                {"sentences": [{"english": "...", "portuguese": "..."}, {"english": "...", "portuguese": "..."}, {"english": "...", "portuguese": "..."}, {"english": "...", "portuguese": "..."}]}
                """.formatted(words.strip());

        return parseSentences(generate(prompt, 0.4f));
    }

    public WordEnrichment enrichWord(String word) {
        String prompt = """
                Você é um professor de inglês para brasileiros adultos.
                Para a palavra ou expressão em inglês "%s", retorne um JSON com exatamente estes campos:
                - word: a própria palavra ou expressão como foi enviada
                - translation: tradução direta e natural em português brasileiro
                - example: uma frase de exemplo curta e natural em inglês usando a palavra
                - exampleTranslation: tradução em português da frase de exemplo
                Responda SOMENTE com o JSON, sem texto adicional.
                Exemplo de formato: {"word":"...","translation":"...","example":"...","exampleTranslation":"..."}
                """.formatted(word.strip());

        return parseWordEnrichment(generate(prompt, 0.2f));
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

    public ChatResponse chat(String cefrLevel, List<ChatTurn> history, String userMessage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("""
                Você é Alex, um parceiro de conversação em inglês amigável e encorajador, ajudando um adulto brasileiro a praticar inglês.
                Adapte vocabulário, complexidade gramatical e tópicos ao nível CEFR %s.
                Suas respostas em inglês devem ser naturais, conversacionais e adequadas ao nível.

                REGRAS OBRIGATÓRIAS:
                - O campo "text" deve ser SEMPRE em inglês (sua resposta na conversa).
                - O campo "translation" deve ser SEMPRE em português brasileiro (tradução do campo "text").
                - O campo "feedback" deve ser SEMPRE em português brasileiro — nunca em inglês.
                  O feedback comenta brevemente o inglês que o usuário usou na última mensagem: gramática, vocabulário ou pronúncia. Seja encorajador.

                """.formatted(cefrLevel));

        if (!history.isEmpty()) {
            prompt.append("Conversa até agora:\n");
            for (ChatTurn turn : history) {
                String role = "AI".equals(turn.sender()) ? "Alex" : "Usuário";
                prompt.append(role).append(": ").append(turn.content()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Usuário: ").append(userMessage.strip()).append("\n\n");
        prompt.append("""
                Responda SOMENTE com um objeto JSON neste formato exato (sem nenhum texto adicional):
                {"text":"<resposta em INGLÊS>","translation":"<tradução em PORTUGUÊS BRASILEIRO>","feedback":"<comentário em PORTUGUÊS BRASILEIRO>"}

                EXEMPLO DO FORMATO ESPERADO:
                {"text":"That's great! What did you do on the weekend?","translation":"Que ótimo! O que você fez no fim de semana?","feedback":"Muito bem! Sua frase está correta e natural."}

                ATENÇÃO: os campos \\"translation\\" e \\"feedback\\" NUNCA podem estar em inglês.
                """);

        return parseChatResponse(generate(prompt.toString(), 0.7f));
    }

    public TextAnalysisResult analyzeText(String text) {
        String prompt = """
                Você é um professor de inglês para brasileiros adultos.
                Analise o texto em inglês abaixo frase por frase.
                Para cada frase, forneça a tradução em português e uma nota gramatical ou de vocabulário útil para um estudante de nível intermediário.
                Também gere até 5 flashcards com palavras ou expressões importantes do texto.

                Texto:
                %s

                Responda SOMENTE com JSON no seguinte formato (sem texto adicional):
                {
                  "overview": "visão geral do texto em português (2-3 frases)",
                  "analysis": [
                    {"sentence": "frase original", "translation": "tradução", "notes": "nota gramatical ou de vocabulário"}
                  ],
                  "ankiCards": [
                    {"english": "palavra ou expressão", "portuguese": "tradução"}
                  ]
                }
                """.formatted(text.strip());

        return parseTextAnalysis(generate(prompt, 0.3f));
    }

    private ChatResponse parseChatResponse(String text) {
        try {
            if (text == null || text.isBlank()) throw new IllegalStateException("Resposta vazia do modelo");
            String json = stripMarkdown(text);
            JsonNode node = objectMapper.readTree(json);
            return new ChatResponse(
                    node.path("text").asText(),
                    node.path("translation").asText(null),
                    node.path("feedback").asText(null)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar resposta do chat", e);
        }
    }

    private TextAnalysisResult parseTextAnalysis(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Resposta vazia do modelo");
            }
            String json = stripMarkdown(text);
            JsonNode root = objectMapper.readTree(json);

            String overview = root.path("overview").asText("");

            List<SentenceAnalysis> analysis = new ArrayList<>();
            JsonNode analysisNode = root.path("analysis");
            if (analysisNode.isArray()) {
                for (JsonNode n : analysisNode) {
                    String sentence    = n.path("sentence").asText();
                    String translation = n.path("translation").asText();
                    String notes       = n.path("notes").asText(null);
                    if (!sentence.isBlank()) {
                        analysis.add(new SentenceAnalysis(sentence, translation, notes));
                    }
                }
            }

            List<AnkiCard> ankiCards = new ArrayList<>();
            JsonNode cardsNode = root.path("ankiCards");
            if (cardsNode.isArray()) {
                for (JsonNode n : cardsNode) {
                    String english    = n.path("english").asText();
                    String portuguese = n.path("portuguese").asText();
                    if (!english.isBlank() && !portuguese.isBlank()) {
                        ankiCards.add(new AnkiCard(english, portuguese));
                    }
                }
            }

            return new TextAnalysisResult(overview, analysis, ankiCards);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar análise de texto", e);
        }
    }

    private String generate(String prompt, float temperature) {
        var body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "stream", false,
                "format", "json",
                "options", Map.of("temperature", temperature)
        );

        try {
            String raw = restClient.post()
                    .uri(baseUrl + "/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode response = objectMapper.readTree(raw);

            if (response == null || !response.path("done").asBoolean()) {
                throw new BadGatewayException("Serviço de IA retornou resposta inválida.");
            }

            String text = response.path("message").path("content").asText();
            log.debug("Ollama raw response: {}", text);
            return text;
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ollama API error: {}", e.getMessage(), e);
            throw new BadGatewayException("Serviço de IA temporariamente indisponível. Tente novamente mais tarde.", e);
        }
    }

    private WordEnrichment parseWordEnrichment(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Resposta vazia do modelo");
            }
            String json = stripMarkdown(text);
            JsonNode node = objectMapper.readTree(json);
            return new WordEnrichment(
                    node.path("word").asText(),
                    node.path("translation").asText(),
                    node.path("example").asText(null),
                    node.path("exampleTranslation").asText(null)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar enriquecimento da palavra", e);
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
