CREATE TABLE IF NOT EXISTS tasks
(
    id               UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    project_id       UUID         NOT NULL REFERENCES projects (id),
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    status           VARCHAR(50)  NOT NULL DEFAULT 'TODO',
    priority         VARCHAR(50)  NOT NULL DEFAULT 'MEDIUM',
    assignee_user_id UUID,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);