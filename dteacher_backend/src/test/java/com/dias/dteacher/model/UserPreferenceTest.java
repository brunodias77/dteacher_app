package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserPreferenceTest {

    @Test
    void shouldHaveDefaultValues() {
        UserPreference pref = UserPreference.builder().build();

        assertThat(pref.getAccent()).isEqualTo("lime");
        assertThat(pref.getDensity()).isEqualTo("cozy");
        assertThat(pref.getUppercaseLevel()).isEqualTo("labels");
        assertThat(pref.getShowStreakBar()).isTrue();
        assertThat(pref.getDefaultCefr()).isEqualTo("B1");
        assertThat(pref.getLastActiveTab()).isEqualTo("generator");
    }

    @Test
    void shouldBuildWithCustomValues() {
        UserPreference pref = UserPreference.builder()
                .accent("cyan")
                .density("compact")
                .uppercaseLevel("all")
                .showStreakBar(false)
                .defaultCefr("C1")
                .lastActiveTab("vocabulary")
                .build();

        assertThat(pref.getAccent()).isEqualTo("cyan");
        assertThat(pref.getDensity()).isEqualTo("compact");
        assertThat(pref.getUppercaseLevel()).isEqualTo("all");
        assertThat(pref.getShowStreakBar()).isFalse();
        assertThat(pref.getDefaultCefr()).isEqualTo("C1");
        assertThat(pref.getLastActiveTab()).isEqualTo("vocabulary");
    }

    @Test
    void preUpdateShouldSetUpdatedAt() throws Exception {
        UserPreference pref = new UserPreference();

        invokePrivate(pref, "preUpdate");

        assertThat(pref.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldLinkToUser() {
        User user = User.builder().email("a@b.com").passwordHash("h").build();
        UserPreference pref = UserPreference.builder().user(user).build();

        assertThat(pref.getUser()).isEqualTo(user);
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
