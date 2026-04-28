package com.dias.dteacher.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_study_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStudyLog {

    @EmbeddedId
    private DailyStudyLogId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cards_reviewed", nullable = false)
    @Builder.Default
    private Integer cardsReviewed = 0;

    @Column(name = "words_added", nullable = false)
    @Builder.Default
    private Integer wordsAdded = 0;
}
