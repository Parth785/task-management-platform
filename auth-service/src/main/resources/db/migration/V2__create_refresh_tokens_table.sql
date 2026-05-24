CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id     UUID         NOT NULL REFERENCES users (id),
    token       TEXT         NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);