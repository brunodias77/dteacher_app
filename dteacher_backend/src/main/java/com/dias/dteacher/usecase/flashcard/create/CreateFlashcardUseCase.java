package com.dias.dteacher.usecase.flashcard.create;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.FlashcardError;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.Flashcard;
import com.dias.dteacher.model.User;
import com.dias.dteacher.repository.FlashcardRepository;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateFlashcardUseCase implements UseCase<CreateFlashcardRequest, CreateFlashcardResponse> {

    private static final int SOURCE_MAX = 30;

    private final UserRepository      userRepository;
    private final FlashcardRepository flashcardRepository;

    @Override
    @Transactional
    public Either<Notification, CreateFlashcardResponse> execute(CreateFlashcardRequest request) {
        Notification notification = Notification.create();
        validate(request, notification);
        if (notification.hasError()) return Either.left(notification);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.of("User", request.email()));

        String source = (request.source() == null || request.source().isBlank())
                ? "manual"
                : request.source().strip();

        Flashcard flashcard = Flashcard.builder()
                .user(user)
                .english(request.english().strip())
                .portuguese(request.portuguese().strip())
                .source(source)
                .build();

        flashcardRepository.save(flashcard);

        return Either.right(new CreateFlashcardResponse(
                flashcard.getId(),
                flashcard.getEnglish(),
                flashcard.getPortuguese(),
                flashcard.getSource(),
                flashcard.getNextReview(),
                flashcard.getIntervalDays(),
                flashcard.getEaseFactor(),
                flashcard.getRepetitions(),
                flashcard.getCreatedAt()
        ));
    }

    private void validate(CreateFlashcardRequest req, Notification n) {
        if (req.english() == null || req.english().isBlank())
            n.append(FlashcardError.ENGLISH_REQUIRED);

        if (req.portuguese() == null || req.portuguese().isBlank())
            n.append(FlashcardError.PORTUGUESE_REQUIRED);

        if (req.source() != null && req.source().length() > SOURCE_MAX)
            n.append(FlashcardError.SOURCE_TOO_LONG);
    }
}
