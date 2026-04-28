CREATE TABLE chat_sessions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    mode            VARCHAR(10) NOT NULL DEFAULT 'chat',
    cefr_level      VARCHAR(2)  NOT NULL DEFAULT 'B1',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_message_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_sessions_user ON chat_sessions(user_id, last_message_at DESC);

CREATE TABLE chat_messages (
    id         UUID       PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID       NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    sender     VARCHAR(5) NOT NULL,
    content    TEXT       NOT NULL,
    translation TEXT,
    feedback   TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_messages_session ON chat_messages(session_id, created_at);
