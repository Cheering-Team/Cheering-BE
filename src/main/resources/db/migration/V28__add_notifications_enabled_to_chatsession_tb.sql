ALTER TABLE chat_session_tb
    ADD COLUMN notifications_enabled BOOLEAN DEFAULT TRUE NOT NULL;

UPDATE chat_session_tb
SET notifications_enabled = TRUE;