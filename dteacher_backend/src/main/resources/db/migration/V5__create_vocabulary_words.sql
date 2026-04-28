CREATE TABLE vocabulary_words (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    word                VARCHAR(150) NOT NULL,
    translation         VARCHAR(150) NOT NULL,
    example             TEXT,
    example_translation TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vocabulary_user ON vocabulary_words(user_id, created_at DESC);

CREATE UNIQUE INDEX idx_vocabulary_user_word ON vocabulary_words(user_id, lower(word));
