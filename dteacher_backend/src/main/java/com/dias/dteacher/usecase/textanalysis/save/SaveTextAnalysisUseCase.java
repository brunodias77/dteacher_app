package com.dias.dteacher.usecase.textanalysis.save;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.TextAnalysisError;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.TextAnalysis;
import com.dias.dteacher.model.User;
import com.dias.dteacher.repository.TextAnalysisRepository;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveTextAnalysisUseCase implements UseCase<SaveTextAnalysisRequest, SaveTextAnalysisResponse> {

    private final UserRepository         userRepository;
    private final TextAnalysisRepository textAnalysisRepository;

    @Override
    @Transactional
    public Either<Notification, SaveTextAnalysisResponse> execute(SaveTextAnalysisRequest request) {
        Notification notification = Notification.create();

        if (request.originalText() == null || request.originalText().isBlank()) {
            notification.append(TextAnalysisError.TEXT_REQUIRED);
            return Either.left(notification);
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.of("User", request.email()));

        TextAnalysis entity = TextAnalysis.builder()
                .user(user)
                .originalText(request.originalText())
                .overview(request.overview())
                .analysisData(request.analysisData() != null ? request.analysisData() : "[]")
                .ankiCards(request.ankiCards()   != null ? request.ankiCards()   : "[]")
                .build();

        textAnalysisRepository.save(entity);

        return Either.right(new SaveTextAnalysisResponse(entity.getId(), entity.getCreatedAt()));
    }
}
