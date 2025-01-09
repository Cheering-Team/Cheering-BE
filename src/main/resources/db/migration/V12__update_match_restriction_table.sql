-- 1. 기존 시퀀스 삭제
DROP SEQUENCE IF EXISTS match_restriction_tb_match_restriction_id_seq CASCADE;

-- 2. 기존 테이블 삭제
DROP TABLE IF EXISTS match_restriction_tb CASCADE;

-- 3. 새로운 시퀀스 생성
CREATE SEQUENCE match_restriction_tb_seq
    increment by 50;

-- 4. 새로운 테이블 생성
CREATE TABLE match_restriction_tb (
                                      match_restriction_id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('match_restriction_tb_seq'),
                                      user_id BIGINT NOT NULL,
                                      match_id BIGINT NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      FOREIGN KEY (user_id) REFERENCES user_tb (user_id) ON DELETE CASCADE,
                                      FOREIGN KEY (match_id) REFERENCES match_tb (match_id) ON DELETE CASCADE
);

-- 5. 인덱스 생성
CREATE INDEX idx_match_restriction_user_id ON match_restriction_tb (user_id);
CREATE INDEX idx_match_restriction_match_id ON match_restriction_tb (match_id);
