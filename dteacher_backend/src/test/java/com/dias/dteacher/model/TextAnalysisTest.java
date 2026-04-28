package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TextAnalysisTest {

    @Test
    void shouldHaveDefaultJsonArrays() {
        TextAnalysis analysis = TextAnalysis.builder().build();

        assertThat(analysis.getAnalysisData()).isEqualTo("[]");
        assertThat(analysis.getAnkiCards()).isEqualTo("[]");
    }

    @Test
    void shouldBuildWithAllFields() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();
        String analysisData = "[{\"word\":\"run\"}]";
        String ankiCards = "[{\"front\":\"run\",\"back\":\"correr\"}]";

        TextAnalysis analysis = TextAnalysis.builder()
                .user(user)
                .originalText("I love to run every morning.")
                .overview("Texto sobre hábitos saudáveis.")
                .analysisData(analysisData)
                .ankiCards(ankiCards)
                .build();

        assertThat(analysis.getUser()).isEqualTo(user);
        assertThat(analysis.getOriginalText()).isEqualTo("I love to run every morning.");
        assertThat(analysis.getOverview()).isEqualTo("Texto sobre hábitos saudáveis.");
        assertThat(analysis.getAnalysisData()).isEqualTo(analysisData);
        assertThat(analysis.getAnkiCards()).isEqualTo(ankiCards);
    }

    @Test
    void shouldAllowNullOverview() {
        TextAnalysis analysis = TextAnalysis.builder().originalText("some text").build();

        assertThat(analysis.getOverview()).isNull();
    }

    @Test
    void prePersistShouldSetCreatedAt() throws Exception {
        TextAnalysis analysis = new TextAnalysis();

        invokePrivate(analysis, "prePersist");

        assertThat(analysis.getCreatedAt()).isNotNull();
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
