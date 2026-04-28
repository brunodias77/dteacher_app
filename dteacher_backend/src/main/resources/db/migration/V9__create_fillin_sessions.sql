CREATE TABLE fillin_sessions (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cefr_level  VARCHAR(2)  NOT NULL,
    words_input TEXT        NOT NULL,
    total       INTEGER     NOT NULL,
    correct     INTEGER     NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
