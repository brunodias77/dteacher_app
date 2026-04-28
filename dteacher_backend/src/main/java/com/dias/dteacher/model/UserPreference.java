package com.dias.dteacher.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String accent = "lime";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String density = "cozy";

    @Column(name = "uppercase_level", nullable = false, length = 20)
    @Builder.Default
    private String uppercaseLevel = "labels";

    @Column(name = "show_streak_bar", nullable = false)
    @Builder.Default
    private Boolean showStreakBar = true;

    @Column(name = "default_cefr", nullable = false, length = 2)
    @Builder.Default
    private String defaultCefr = "B1";

    @Column(name = "last_active_tab", nullable = false, length = 30)
    @Builder.Default
    private String lastActiveTab = "generator";

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
