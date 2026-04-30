package com.dias.dteacher.usecase.flashcard.list;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.model.Flashcard;
import com.dias.dteacher.repository.FlashcardRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListFlashcardsUseCase implements UseCase<ListFlashcardsRequest, ListFlashcardsResponse> {

    private final FlashcardRepository flashcardRepository;

    @Override
    @Transactional(readOnly = true)
    public Either<Notification, ListFlashcardsResponse> execute(ListFlashcardsRequest request) {
        List<Flashcard> cards = flashcardRepository.findByUserEmailOrderByNextReviewAsc(request.email());

        OffsetDateTime now = OffsetDateTime.now();
        int totalDue = (int) cards.stream()
                .filter(c -> !c.getNextReview().isAfter(now))
                .count();

        List<ListFlashcardsResponse.FlashcardDto> dtos = cards.stream()
                .map(c -> new ListFlashcardsResponse.FlashcardDto(
                        c.getId(),
                        c.getEnglish(),
                        c.getPortuguese(),
                        c.getSource(),
                        c.getNextReview(),
                        c.getIntervalDays(),
                        c.getEaseFactor(),
                        c.getRepetitions(),
                        c.getCreatedAt()
                ))
                .toList();

        return Either.right(new ListFlashcardsResponse(dtos, dtos.size(), totalDue));
    }
}
