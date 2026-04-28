package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class StudyStreakTest {

    @Test
    void shouldHaveDefaultValues() {
        StudyStreak streak = StudyStreak.builder().build();

        assertThat(streak.getCurrentStreak()).isEqualTo(0);
        assertThat(streak.getLongestStreak()).isEqualTo(0);
        assertThat(streak.getTotalDays()).isEqualTo(0);
        assertThat(streak.getLastStudyDate()).isNull();
    }

    @Test
    void shouldBuildWithAllFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();
        LocalDate today = LocalDate.now();

        StudyStreak streak = StudyStreak.builder()
                .user(user)
                .currentStreak(5)
                .longestStreak(10)
                .lastStudyDate(today)
                .totalDays(30)
                .build();

        assertThat(streak.getUser()).isEqualTo(user);
        assertThat(streak.getCurrentStreak()).isEqualTo(5);
        assertThat(streak.getLongestStreak()).isEqualTo(10);
        assertThat(streak.getLastStudyDate()).isEqualTo(today);
        assertThat(streak.getTotalDays()).isEqualTo(30);
    }

    @Test
    void preUpdateShouldSetUpdatedAt() throws Exception {
        StudyStreak streak = new StudyStreak();

        invokePrivate(streak, "preUpdate");

        assertThat(streak.getUpdatedAt()).isNotNull();
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
