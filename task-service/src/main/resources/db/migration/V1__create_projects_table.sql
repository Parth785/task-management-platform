CREATE TABLE IF NOT EXISTS projects
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    owner_user_id UUID         NOT NULL,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);