-- V2__Add_manager_column_to_meet_tb.sql
ALTER TABLE meet_tb
    ADD COLUMN manager_id BIGINT;

ALTER TABLE meet_tb
    ADD CONSTRAINT fk_meet_tb_manager
        FOREIGN KEY (manager_id) REFERENCES fan_tb(fan_id);