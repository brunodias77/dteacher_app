package com.dias.dteacher.repository;

import com.dias.dteacher.model.VocabularyWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VocabularyWordRepository extends JpaRepository<VocabularyWord, UUID> {

    boolean existsByUserIdAndWordIgnoreCase(UUID userId, String word);

    List<VocabularyWord> findByUserEmailOrderByCreatedAtDesc(String email);

    @Query("""
            SELECT w FROM VocabularyWord w
            WHERE w.user.email = :email
              AND (lower(w.word) LIKE lower(concat('%', :q, '%'))
                OR lower(w.translation) LIKE lower(concat('%', :q, '%')))
            ORDER BY w.createdAt DESC
            """)
    List<VocabularyWord> searchByUserEmailAndTerm(@Param("email") String email, @Param("q") String q);
}
