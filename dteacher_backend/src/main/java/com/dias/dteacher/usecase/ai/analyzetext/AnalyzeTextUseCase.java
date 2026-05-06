package com.dias.dteacher.usecase.ai.analyzetext;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.TextAnalysisError;
import com.dias.dteacher.service.OllamaService;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyzeTextUseCase implements UseCase<AnalyzeTextRequest, AnalyzeTextResponse> {

    private static final int TEXT_MAX = 3000;

    private final OllamaService ollamaService;

    @Override
    public Either<Notification, AnalyzeTextResponse> execute(AnalyzeTextRequest request) {
        Notification notification = Notification.create();

        if (request.text() == null || request.text().isBlank()) {
            notification.append(TextAnalysisError.TEXT_REQUIRED);
            return Either.left(notification);
        }
        if (request.text().length() > TEXT_MAX) {
            notification.append(TextAnalysisError.TEXT_TOO_LONG);
            return Either.left(notification);
        }

        OllamaService.TextAnalysisResult result = ollamaService.analyzeText(request.text());

        List<AnalyzeTextResponse.SentenceAnalysis> analysis = result.analysis().stream()
                .map(a -> new AnalyzeTextResponse.SentenceAnalysis(a.sentence(), a.translation(), a.notes()))
                .toList();

        List<AnalyzeTextResponse.AnkiCard> ankiCards = result.ankiCards().stream()
                .map(c -> new AnalyzeTextResponse.AnkiCard(c.english(), c.portuguese()))
                .toList();

        return Either.right(new AnalyzeTextResponse(result.overview(), analysis, ankiCards));
    }
}
