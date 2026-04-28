package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class VocabularyWordTest {

    @Test
    void shouldBuildWithRequiredFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();

        VocabularyWord word = VocabularyWord.builder()
                .user(user)
                .word("achievement")
                .translation("conquista")
                .build();

        assertThat(word.getUser()).isEqualTo(user);
        assertThat(word.getWord()).isEqualTo("achievement");
        assertThat(word.getTranslation()).isEqualTo("conquista");
        assertThat(word.getExample()).isNull();
        assertThat(word.getExampleTranslation()).isNull();
    }

    @Test
    void shouldBuildWithOptionalFields() {
        VocabularyWord word = VocabularyWord.builder()
                .word("run")
                .translation("correr")
                .example("I run every morning.")
                .exampleTranslation("Eu corro toda manhã.")
                .build();

        assertThat(word.getExample()).isEqualTo("I run every morning.");
        assertThat(word.getExampleTranslation()).isEqualTo("Eu corro toda manhã.");
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        VocabularyWord word = new VocabularyWord();

        invokePrivate(word, "prePersist");

        assertThat(word.getCreatedAt()).isNotNull();
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
