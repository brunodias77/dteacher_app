CREATE TABLE text_analyses (
    id            UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    original_text TEXT  NOT NULL,
    overview      TEXT,
    analysis_data JSONB NOT NULL DEFAULT '[]',
    anki_cards    JSONB NOT NULL DEFAULT '[]',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_text_analyses_user ON text_analyses(user_id, created_at DESC);
