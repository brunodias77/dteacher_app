package com.dias.dteacher.usecase.vocabulary.list;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.model.VocabularyWord;
import com.dias.dteacher.repository.VocabularyWordRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListVocabularyWordsUseCase implements UseCase<ListVocabularyWordsRequest, ListVocabularyWordsResponse> {

    private final VocabularyWordRepository vocabularyWordRepository;

    @Override
    @Transactional(readOnly = true)
    public Either<Notification, ListVocabularyWordsResponse> execute(ListVocabularyWordsRequest request) {
        String q = request.q();
        List<VocabularyWord> words = (q != null && !q.isBlank())
                ? vocabularyWordRepository.searchByUserEmailAndTerm(request.email(), q.strip())
                : vocabularyWordRepository.findByUserEmailOrderByCreatedAtDesc(request.email());

        List<ListVocabularyWordsResponse.WordDto> dtos = words.stream()
                .map(w -> new ListVocabularyWordsResponse.WordDto(
                        w.getId(),
                        w.getWord(),
                        w.getTranslation(),
                        w.getExample(),
                        w.getExampleTranslation(),
                        w.getCreatedAt()
                ))
                .toList();

        return Either.right(new ListVocabularyWordsResponse(dtos, dtos.size()));
    }
}
