-- =============================================================
-- btree_english – PostgreSQL Schema
-- =============================================================
-- Substitui o Firestore do protótipo original.
-- Convenções: snake_case, UUIDs como PK, timestamps com fuso.
-- =============================================================

-- Extensão para geração de UUID v4
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================
-- 1. USUÁRIOS
-- =============================================================
CREATE TABLE users (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(100),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =============================================================
-- 2. PREFERÊNCIAS DE INTERFACE (por usuário)
-- Espelha o objeto `tweaks` do App e o nível CEFR do chat.
-- =============================================================
CREATE TABLE user_preferences (
    user_id           UUID        PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    accent            VARCHAR(20) NOT NULL DEFAULT 'lime',       -- lime | amber | cyan | magenta | white
    density           VARCHAR(20) NOT NULL DEFAULT 'cozy',       -- compact | cozy | roomy
    uppercase_level   VARCHAR(20) NOT NULL DEFAULT 'labels',     -- none | labels | headings | all
    show_streak_bar   BOOLEAN     NOT NULL DEFAULT TRUE,
    default_cefr      VARCHAR(2)  NOT NULL DEFAULT 'B1',         -- A1 | A2 | B1 | B2 | C1 | C2
    last_active_tab   VARCHAR(30) NOT NULL DEFAULT 'generator',  -- id da aba
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =============================================================
-- 3. FLASHCARDS (Deck SRS)
-- Algoritmo simplificado: nextReview + intervalo + easeFactor
-- (compatível com o protótipo; pode evoluir para SM-2 completo)
-- =============================================================
CREATE TABLE flashcards (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    english       TEXT        NOT NULL,
    portuguese    TEXT        NOT NULL,
    next_review   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    interval_days INTEGER     NOT NULL DEFAULT 0,    -- dias até próxima revisão
    ease_factor   NUMERIC(4,2) NOT NULL DEFAULT 2.50,
    repetitions   INTEGER     NOT NULL DEFAULT 0,
    source        VARCHAR(30) NOT NULL DEFAULT 'manual',
    -- 'generator' | 'vocabulary' | 'text_study' | 'fill_in' | 'manual'
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_flashcards_user_next_review
    ON flashcards(user_id, next_review);

-- =============================================================
-- 4. VOCABULÁRIO
-- Palavras salvas pelo usuário com tradução e exemplo de uso.
-- =============================================================
CREATE TABLE vocabulary_words (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    word                VARCHAR(150) NOT NULL,
    translation         VARCHAR(150) NOT NULL,
    example             TEXT,
    example_translation TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vocabulary_user
    ON vocabulary_words(user_id, created_at DESC);

CREATE UNIQUE INDEX idx_vocabulary_user_word
    ON vocabulary_words(user_id, lower(word));

-- =============================================================
-- 5. STREAK / ESTATÍSTICAS DE ESTUDO
-- =============================================================
CREATE TABLE study_streaks (
    user_id         UUID    PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    current_streak  INTEGER NOT NULL DEFAULT 0,
    longest_streak  INTEGER NOT NULL DEFAULT 0,
    last_study_date DATE,
    total_days      INTEGER NOT NULL DEFAULT 0,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Log diário: quantos cartões revisados e palavras adicionadas
CREATE TABLE daily_study_log (
    user_id         UUID    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    study_date      DATE    NOT NULL,
    cards_reviewed  INTEGER NOT NULL DEFAULT 0,
    words_added     INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, study_date)
);

-- =============================================================
-- 6. SESSÕES DE CHAT (Tutor IA)
-- Cada conversa – seja modo 'chat' (prática) ou 'teacher' (dúvidas).
-- =============================================================
CREATE TABLE chat_sessions (
    id              UUID       PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    mode            VARCHAR(10) NOT NULL DEFAULT 'chat',  -- 'chat' | 'teacher'
    cefr_level      VARCHAR(2)  NOT NULL DEFAULT 'B1',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_message_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_sessions_user
    ON chat_sessions(user_id, last_message_at DESC);

-- =============================================================
-- 7. MENSAGENS DO CHAT
-- Histórico completo da conversa com feedback do professor.
-- =============================================================
CREATE TABLE chat_messages (
    id          UUID       PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id  UUID       NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    sender      VARCHAR(5) NOT NULL,  -- 'USER' | 'AI'
    content     TEXT       NOT NULL,
    translation TEXT,       -- tradução PT-BR (mensagens da IA no modo chat)
    feedback    TEXT,       -- feedback do professor (mensagens do usuário no modo chat)
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_messages_session
    ON chat_messages(session_id, created_at);

-- =============================================================
-- 8. ANÁLISES DE TEXTO SALVAS  (TextStudyScreen)
-- Permite ao usuário rever análises anteriores.
-- =============================================================
CREATE TABLE text_analyses (
    id            UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    original_text TEXT  NOT NULL,
    overview      TEXT,
    -- Armazena o array `analysis` e `ankiCards` gerados pela IA
    analysis_data JSONB NOT NULL DEFAULT '[]',
    anki_cards    JSONB NOT NULL DEFAULT '[]',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_text_analyses_user
    ON text_analyses(user_id, created_at DESC);

-- =============================================================
-- 9. HISTÓRICO DE EXERCÍCIOS FILL-IN  (FillInScreen)
-- Registra as sessões de fill-in para acompanhar o progresso.
-- =============================================================
CREATE TABLE fillin_sessions (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cefr_level   VARCHAR(2)  NOT NULL,
    words_input  TEXT        NOT NULL,   -- palavras que o usuário digitou
    total        INTEGER     NOT NULL,
    correct      INTEGER     NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =============================================================
-- TRIGGER: atualiza updated_at automaticamente
-- =============================================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_flashcards_updated_at
    BEFORE UPDATE ON flashcards
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_study_streaks_updated_at
    BEFORE UPDATE ON study_streaks
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
