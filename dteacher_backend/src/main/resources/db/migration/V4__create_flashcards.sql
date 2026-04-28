CREATE TABLE flashcards (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    english       TEXT         NOT NULL,
    portuguese    TEXT         NOT NULL,
    next_review   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    interval_days INTEGER      NOT NULL DEFAULT 0,
    ease_factor   NUMERIC(4,2) NOT NULL DEFAULT 2.50,
    repetitions   INTEGER      NOT NULL DEFAULT 0,
    source        VARCHAR(30)  NOT NULL DEFAULT 'manual',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_flashcards_user_next_review ON flashcards(user_id, next_review);
