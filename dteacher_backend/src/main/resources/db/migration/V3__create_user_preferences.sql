CREATE TABLE user_preferences (
    user_id           UUID        PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    accent            VARCHAR(20) NOT NULL DEFAULT 'lime',
    density           VARCHAR(20) NOT NULL DEFAULT 'cozy',
    uppercase_level   VARCHAR(20) NOT NULL DEFAULT 'labels',
    show_streak_bar   BOOLEAN     NOT NULL DEFAULT TRUE,
    default_cefr      VARCHAR(2)  NOT NULL DEFAULT 'B1',
    last_active_tab   VARCHAR(30) NOT NULL DEFAULT 'generator',
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
