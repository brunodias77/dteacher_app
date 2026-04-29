package com.dias.dteacher.usecase.generator;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.GeneratorError;
import com.dias.dteacher.service.GeminiService;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateSentencesUseCase implements UseCase<GenerateSentencesRequest, GenerateSentencesResponse> {

    private static final int WORDS_MAX = 200;

    private final GeminiService geminiService;

    @Override
    public Either<Notification, GenerateSentencesResponse> execute(GenerateSentencesRequest request) {
        Notification notification = Notification.create();

        if (request.words() == null || request.words().isBlank()) {
            notification.append(GeneratorError.WORDS_REQUIRED);
            return Either.left(notification);
        }
        if (request.words().length() > WORDS_MAX) {
            notification.append(GeneratorError.WORDS_TOO_LONG);
            return Either.left(notification);
        }

        try {
            var pairs = geminiService.generateSentences(request.words());
            var sentences = pairs.stream()
                    .map(p -> new GenerateSentencesResponse.Sentence(p.english(), p.portuguese()))
                    .toList();
            return Either.right(new GenerateSentencesResponse(sentences));
        } catch (Exception e) {
            log.error("Gemini error for words='{}': {}", request.words(), e.getMessage());
            notification.append(GeneratorError.GEMINI_UNAVAILABLE);
            return Either.left(notification);
        }
    }
}
