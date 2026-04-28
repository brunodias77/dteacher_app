package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSessionTest {

    @Test
    void shouldHaveDefaultValues() {
        ChatSession session = ChatSession.builder().build();

        assertThat(session.getMode()).isEqualTo("chat");
        assertThat(session.getCefrLevel()).isEqualTo("B1");
    }

    @Test
    void shouldBuildWithAllFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();

        ChatSession session = ChatSession.builder()
                .user(user)
                .mode("teacher")
                .cefrLevel("C1")
                .build();

        assertThat(session.getUser()).isEqualTo(user);
        assertThat(session.getMode()).isEqualTo("teacher");
        assertThat(session.getCefrLevel()).isEqualTo("C1");
    }

    @Test
    void prePersistShouldSetTimestamps() throws Exception {
        ChatSession session = new ChatSession();

        invokePrivate(session, "prePersist");

        assertThat(session.getCreatedAt()).isNotNull();
        assertThat(session.getLastMessageAt()).isNotNull();
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
