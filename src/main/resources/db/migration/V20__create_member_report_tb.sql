CREATE TABLE member_report_tb (
                                  member_report_id SERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL,
                                  report_reason VARCHAR(2000),
                                  writer_id BIGINT REFERENCES fan_tb(fan_id) ON DELETE CASCADE,
                                  reported_id BIGINT REFERENCES fan_tb(fan_id) ON DELETE CASCADE,
                                  meet_id BIGINT REFERENCES meet_tb(meet_id) ON DELETE CASCADE,
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);