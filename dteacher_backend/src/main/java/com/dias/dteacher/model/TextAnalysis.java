package com.dias.dteacher.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "text_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextAnalysis {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_data", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private String analysisData = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "anki_cards", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private String ankiCards = "[]";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
