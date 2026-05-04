package com.dias.dteacher.repository;

import com.dias.dteacher.model.VocabularyWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VocabularyWordRepository extends JpaRepository<VocabularyWord, UUID> {

    boolean existsByUserIdAndWordIgnoreCase(UUID userId, String word);

    List<VocabularyWord> findByUserEmailOrderByCreatedAtDesc(String email);
}
