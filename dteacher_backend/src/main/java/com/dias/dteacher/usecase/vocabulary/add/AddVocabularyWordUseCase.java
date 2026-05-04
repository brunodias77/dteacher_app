package com.dias.dteacher.usecase.vocabulary.add;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.VocabularyError;
import com.dias.dteacher.exception.ConflictException;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.Flashcard;
import com.dias.dteacher.model.User;
import com.dias.dteacher.model.VocabularyWord;
import com.dias.dteacher.repository.FlashcardRepository;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.repository.VocabularyWordRepository;
import com.dias.dteacher.service.OllamaService;
import com.dias.dteacher.service.StudyLogService;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddVocabularyWordUseCase implements UseCase<AddVocabularyWordRequest, AddVocabularyWordResponse> {

    private static final int WORD_MAX = 150;

    private final UserRepository         userRepository;
    private final VocabularyWordRepository vocabularyWordRepository;
    private final FlashcardRepository    flashcardRepository;
    private final OllamaService          ollamaService;
    private final StudyLogService        studyLogService;

    @Override
    @Transactional
    public Either<Notification, AddVocabularyWordResponse> execute(AddVocabularyWordRequest request) {
        Notification notification = Notification.create();

        if (request.word() == null || request.word().isBlank()) {
            notification.append(VocabularyError.WORD_REQUIRED);
            return Either.left(notification);
        }
        if (request.word().length() > WORD_MAX) {
            notification.append(VocabularyError.WORD_TOO_LONG);
            return Either.left(notification);
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.of("User", request.email()));

        String word = request.word().strip();

        if (vocabularyWordRepository.existsByUserIdAndWordIgnoreCase(user.getId(), word)) {
            throw new ConflictException(VocabularyError.WORD_DUPLICATE.message());
        }

        OllamaService.WordEnrichment enrichment = ollamaService.enrichWord(word);

        VocabularyWord vocabularyWord = VocabularyWord.builder()
                .user(user)
                .word(word)
                .translation(enrichment.translation())
                .example(enrichment.example())
                .exampleTranslation(enrichment.exampleTranslation())
                .build();
        vocabularyWordRepository.save(vocabularyWord);

        Flashcard flashcard = Flashcard.builder()
                .user(user)
                .english(word)
                .portuguese(enrichment.translation())
                .source("vocabulary")
                .build();
        flashcardRepository.save(flashcard);

        studyLogService.recordWordAdded(request.email());

        return Either.right(new AddVocabularyWordResponse(
                vocabularyWord.getId(),
                vocabularyWord.getWord(),
                vocabularyWord.getTranslation(),
                vocabularyWord.getExample(),
                vocabularyWord.getExampleTranslation(),
                vocabularyWord.getCreatedAt()
        ));
    }
}
