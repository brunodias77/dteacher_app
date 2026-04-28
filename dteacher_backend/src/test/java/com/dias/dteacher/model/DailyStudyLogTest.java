package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DailyStudyLogTest {

    @Test
    void shouldHaveDefaultValues() {
        DailyStudyLog log = DailyStudyLog.builder().build();

        assertThat(log.getCardsReviewed()).isEqualTo(0);
        assertThat(log.getWordsAdded()).isEqualTo(0);
    }

    @Test
    void shouldBuildWithAllFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();
        DailyStudyLogId id = new DailyStudyLogId(UUID.randomUUID(), LocalDate.now());

        DailyStudyLog log = DailyStudyLog.builder()
                .id(id)
                .user(user)
                .cardsReviewed(15)
                .wordsAdded(5)
                .build();

        assertThat(log.getId()).isEqualTo(id);
        assertThat(log.getUser()).isEqualTo(user);
        assertThat(log.getCardsReviewed()).isEqualTo(15);
        assertThat(log.getWordsAdded()).isEqualTo(5);
    }
}
