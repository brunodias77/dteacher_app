package com.dias.dteacher.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String english;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String portuguese;

    @Column(name = "next_review", nullable = false)
    private OffsetDateTime nextReview;

    @Column(name = "interval_days", nullable = false)
    @Builder.Default
    private Integer intervalDays = 0;

    @Column(name = "ease_factor", nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal easeFactor = new BigDecimal("2.50");

    @Column(nullable = false)
    @Builder.Default
    private Integer repetitions = 0;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String source = "manual";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (nextReview == null) nextReview = OffsetDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
