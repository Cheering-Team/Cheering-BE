BEGIN;

UPDATE chat_tb
SET type = 'SYSTEM'
WHERE type IN ('SYSTEM_ENTER', 'SYSTEM_EXIT');

DO $$ BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_type
        WHERE typname = 'chat_type'
    ) THEN
CREATE TYPE chat_type_new AS ENUM ('MESSAGE', 'SYSTEM', 'JOIN_REQUEST');

ALTER TABLE chat_tb ALTER COLUMN type TYPE chat_type_new USING type::text::chat_type_new;

DROP TYPE chat_type;
ALTER TYPE chat_type_new RENAME TO chat_type;
END IF;
END $$;