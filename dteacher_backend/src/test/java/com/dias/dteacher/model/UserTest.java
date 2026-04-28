package com.dias.dteacher.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldBuildUserWithAllFields() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = User.builder()
                .id(id)
                .email("joao@email.com")
                .passwordHash("hashed_password")
                .displayName("João Silva")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo("joao@email.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(user.getDisplayName()).isEqualTo("João Silva");
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void prePersistShouldSetCreatedAtAndUpdatedAt() throws Exception {
        User user = new User();

        invokePrivate(user, "prePersist");

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void preUpdateShouldRefreshUpdatedAt() throws Exception {
        User user = new User();
        invokePrivate(user, "prePersist");
        OffsetDateTime initial = user.getUpdatedAt();

        invokePrivate(user, "preUpdate");

        assertThat(user.getUpdatedAt()).isAfterOrEqualTo(initial);
    }

    @Test
    void shouldAllowNullDisplayName() {
        User user = User.builder()
                .email("sem@nome.com")
                .passwordHash("hash")
                .build();

        assertThat(user.getDisplayName()).isNull();
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}
