CREATE TABLE match_restriction_tb (
                                      match_restriction_id BIGSERIAL PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      match_id BIGINT NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      FOREIGN KEY (user_id) REFERENCES user_tb (user_id) ON DELETE CASCADE,
                                      FOREIGN KEY (match_id) REFERENCES match_tb (match_id) ON DELETE CASCADE
);

CREATE INDEX idx_match_restriction_user_id ON match_restriction_tb (user_id);
CREATE INDEX idx_match_restriction_match_id ON match_restriction_tb (match_id);
