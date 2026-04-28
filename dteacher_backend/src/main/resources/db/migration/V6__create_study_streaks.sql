CREATE TABLE study_streaks (
    user_id         UUID    PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    current_streak  INTEGER NOT NULL DEFAULT 0,
    longest_streak  INTEGER NOT NULL DEFAULT 0,
    last_study_date DATE,
    total_days      INTEGER NOT NULL DEFAULT 0,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE daily_study_log (
    user_id        UUID    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    study_date     DATE    NOT NULL,
    cards_reviewed INTEGER NOT NULL DEFAULT 0,
    words_added    INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, study_date)
);
