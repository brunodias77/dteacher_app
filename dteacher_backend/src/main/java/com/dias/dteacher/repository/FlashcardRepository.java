package com.dias.dteacher.repository;

import com.dias.dteacher.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {
}
