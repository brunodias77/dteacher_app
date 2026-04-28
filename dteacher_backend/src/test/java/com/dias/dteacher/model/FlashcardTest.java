package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FlashcardTest {

    @Test
    void shouldHaveDefaultValues() {
        Flashcard card = Flashcard.builder().build();

        assertThat(card.getIntervalDays()).isEqualTo(0);
        assertThat(card.getEaseFactor()).isEqualByComparingTo(new BigDecimal("2.50"));
        assertThat(card.getRepetitions()).isEqualTo(0);
        assertThat(card.getSource()).isEqualTo("manual");
    }

    @Test
    void shouldBuildWithAllFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();
        OffsetDateTime now = OffsetDateTime.now();

        Flashcard card = Flashcard.builder()
                .user(user)
                .english("apple")
                .portuguese("maçã")
                .nextReview(now)
                .intervalDays(3)
                .easeFactor(new BigDecimal("2.60"))
                .repetitions(2)
                .source("generator")
                .build();

        assertThat(card.getUser()).isEqualTo(user);
        assertThat(card.getEnglish()).isEqualTo("apple");
        assertThat(card.getPortuguese()).isEqualTo("maçã");
        assertThat(card.getNextReview()).isEqualTo(now);
        assertThat(card.getIntervalDays()).isEqualTo(3);
        assertThat(card.getEaseFactor()).isEqualByComparingTo(new BigDecimal("2.60"));
        assertThat(card.getRepetitions()).isEqualTo(2);
        assertThat(card.getSource()).isEqualTo("generator");
    }

    @Test
    void prePersistShouldSetTimestamps() throws Exception {
        Flashcard card = new Flashcard();

        invokePrivate(card, "prePersist");

        assertThat(card.getCreatedAt()).isNotNull();
        assertThat(card.getUpdatedAt()).isNotNull();
        assertThat(card.getNextReview()).isNotNull();
    }

    @Test
    void prePersistShouldNotOverwriteExistingNextReview() throws Exception {
        OffsetDateTime future = OffsetDateTime.now().plusDays(5);
        Flashcard card = Flashcard.builder().nextReview(future).build();

        invokePrivate(card, "prePersist");

        assertThat(card.getNextReview()).isEqualTo(future);
    }

    @Test
    void preUpdateShouldRefreshUpdatedAt() throws Exception {
        Flashcard card = new Flashcard();
        invokePrivate(card, "prePersist");
        OffsetDateTime initial = card.getUpdatedAt();

        invokePrivate(card, "preUpdate");

        assertThat(card.getUpdatedAt()).isAfterOrEqualTo(initial);
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
