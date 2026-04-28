package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DailyStudyLogIdTest {

    @Test
    void equalIdsShouldBeEqual() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        DailyStudyLogId id1 = new DailyStudyLogId(userId, date);
        DailyStudyLogId id2 = new DailyStudyLogId(userId, date);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void differentUserIdShouldNotBeEqual() {
        LocalDate date = LocalDate.now();

        DailyStudyLogId id1 = new DailyStudyLogId(UUID.randomUUID(), date);
        DailyStudyLogId id2 = new DailyStudyLogId(UUID.randomUUID(), date);

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void differentDateShouldNotBeEqual() {
        UUID userId = UUID.randomUUID();

        DailyStudyLogId id1 = new DailyStudyLogId(userId, LocalDate.now());
        DailyStudyLogId id2 = new DailyStudyLogId(userId, LocalDate.now().minusDays(1));

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldStoreFields() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 4, 27);

        DailyStudyLogId id = new DailyStudyLogId(userId, date);

        assertThat(id.getUserId()).isEqualTo(userId);
        assertThat(id.getStudyDate()).isEqualTo(date);
    }
}
