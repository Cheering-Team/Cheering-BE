ALTER TABLE notification_tb
    ADD COLUMN meet_id BIGINT,
    ADD COLUMN meet_name VARCHAR(255);

ALTER TABLE notification_tb
    ADD CONSTRAINT fk_notification_meet
        FOREIGN KEY (meet_id) REFERENCES meet_tb(meet_id)
            ON DELETE SET NULL;
