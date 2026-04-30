package com.dias.dteacher.repository;

import com.dias.dteacher.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {

    List<Flashcard> findByUserEmailOrderByNextReviewAsc(String email);

    Optional<Flashcard> findByIdAndUserEmail(UUID id, String email);
}
