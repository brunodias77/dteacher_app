package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class FillinSessionTest {

    @Test
    void shouldBuildWithAllFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();

        FillinSession session = FillinSession.builder()
                .user(user)
                .cefrLevel("B2")
                .wordsInput("run, jump, swim")
                .total(3)
                .correct(2)
                .build();

        assertThat(session.getUser()).isEqualTo(user);
        assertThat(session.getCefrLevel()).isEqualTo("B2");
        assertThat(session.getWordsInput()).isEqualTo("run, jump, swim");
        assertThat(session.getTotal()).isEqualTo(3);
        assertThat(session.getCorrect()).isEqualTo(2);
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        FillinSession session = new FillinSession();

        invokePrivate(session, "prePersist");

        assertThat(session.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldAcceptZeroCorrect() {
        FillinSession session = FillinSession.builder()
                .total(5)
                .correct(0)
                .build();

        assertThat(session.getCorrect()).isEqualTo(0);
        assertThat(session.getTotal()).isEqualTo(5);
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
