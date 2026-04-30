package com.dias.dteacher.usecase.flashcard.review;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.FlashcardError;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.Flashcard;
import com.dias.dteacher.repository.FlashcardRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewFlashcardUseCase implements UseCase<ReviewFlashcardRequest, ReviewFlashcardResponse> {

    private static final Set<String> VALID_GRADES = Set.of("again", "hard", "good", "easy");
    private static final BigDecimal MIN_EASE      = new BigDecimal("1.30");

    private final FlashcardRepository flashcardRepository;

    @Override
    @Transactional
    public Either<Notification, ReviewFlashcardResponse> execute(ReviewFlashcardRequest request) {
        Notification notification = Notification.create();

        if (request.grade() == null || !VALID_GRADES.contains(request.grade())) {
            notification.append(FlashcardError.INVALID_GRADE);
            return Either.left(notification);
        }

        Flashcard card = flashcardRepository
                .findByIdAndUserEmail(request.id(), request.email())
                .orElseThrow(() -> NotFoundException.of("Flashcard", request.id().toString()));

        applyGrade(card, request.grade());
        flashcardRepository.save(card);

        return Either.right(new ReviewFlashcardResponse(
                card.getId(),
                card.getEnglish(),
                card.getPortuguese(),
                card.getSource(),
                card.getNextReview(),
                card.getIntervalDays(),
                card.getEaseFactor(),
                card.getRepetitions(),
                card.getCreatedAt()
        ));
    }

    private void applyGrade(Flashcard card, String grade) {
        int reps     = card.getRepetitions();
        int interval = card.getIntervalDays();
        BigDecimal ef = card.getEaseFactor();

        int newInterval;
        BigDecimal newEf;
        int newReps;

        switch (grade) {
            case "again" -> {
                newEf       = ef.subtract(new BigDecimal("0.20")).max(MIN_EASE);
                newInterval = 1;
                newReps     = 0;
            }
            case "hard" -> {
                newEf       = ef.subtract(new BigDecimal("0.15")).max(MIN_EASE);
                newInterval = Math.max(1, (int) Math.round(interval * 1.2));
                newReps     = reps + 1;
            }
            case "good" -> {
                newEf = ef;
                newInterval = switch (reps) {
                    case 0  -> 1;
                    case 1  -> 6;
                    default -> (int) Math.round(interval * ef.doubleValue());
                };
                newReps = reps + 1;
            }
            default -> { // easy
                newEf       = ef.add(new BigDecimal("0.15"));
                newInterval = switch (reps) {
                    case 0  -> 4;
                    case 1  -> 10;
                    default -> (int) Math.round(interval * ef.doubleValue() * 1.3);
                };
                newReps = reps + 1;
            }
        }

        card.setEaseFactor(newEf.setScale(2, RoundingMode.HALF_UP));
        card.setIntervalDays(newInterval);
        card.setRepetitions(newReps);
        card.setNextReview(OffsetDateTime.now().plusDays(newInterval));
    }
}
