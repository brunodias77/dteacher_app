package com.dias.dteacher.usecase.generator;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.GeneratorError;
import com.dias.dteacher.service.OllamaService;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslateWordUseCase implements UseCase<TranslateWordRequest, TranslateWordResponse> {

    private static final int WORD_MAX = 100;

    private final OllamaService ollamaService;

    @Override
    public Either<Notification, TranslateWordResponse> execute(TranslateWordRequest request) {
        Notification notification = Notification.create();

        if (request.word() == null || request.word().isBlank()) {
            notification.append(GeneratorError.WORD_REQUIRED);
            return Either.left(notification);
        }
        if (request.word().length() > WORD_MAX) {
            notification.append(GeneratorError.WORD_TOO_LONG);
            return Either.left(notification);
        }

        var portuguese = ollamaService.translateWord(request.word());
        return Either.right(new TranslateWordResponse(portuguese));
    }
}
