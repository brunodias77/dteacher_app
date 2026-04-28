package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    void shouldBuildWithRequiredFields() {
        ChatSession session = ChatSession.builder().mode("chat").cefrLevel("B1").build();

        ChatMessage message = ChatMessage.builder()
                .session(session)
                .sender("USER")
                .content("Hello, how are you?")
                .build();

        assertThat(message.getSession()).isEqualTo(session);
        assertThat(message.getSender()).isEqualTo("USER");
        assertThat(message.getContent()).isEqualTo("Hello, how are you?");
        assertThat(message.getTranslation()).isNull();
        assertThat(message.getFeedback()).isNull();
    }

    @Test
    void shouldBuildWithOptionalFields() {
        ChatMessage message = ChatMessage.builder()
                .sender("AI")
                .content("I'm fine, thank you!")
                .translation("Estou bem, obrigado!")
                .feedback("Good sentence structure.")
                .build();

        assertThat(message.getTranslation()).isEqualTo("Estou bem, obrigado!");
        assertThat(message.getFeedback()).isEqualTo("Good sentence structure.");
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        ChatMessage message = new ChatMessage();

        invokePrivate(message, "prePersist");

        assertThat(message.getCreatedAt()).isNotNull();
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
