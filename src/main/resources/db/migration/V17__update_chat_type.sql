-- 1. 기존 CHECK 제약조건 제거
ALTER TABLE chat_tb
DROP CONSTRAINT chat_tb_type_check;

-- 2. 데이터 업데이트: SYSTEM_ENTER와 SYSTEM_EXIT를 SYSTEM으로 변경
UPDATE chat_tb
SET type = 'SYSTEM'
WHERE type IN ('SYSTEM_ENTER', 'SYSTEM_EXIT');

-- 3. 새로운 CHECK 제약조건 추가
ALTER TABLE chat_tb
    ADD CONSTRAINT chat_tb_type_check
        CHECK ((type)::text = ANY
    (ARRAY [
    ('MESSAGE'::character varying)::text,
    ('SYSTEM'::character varying)::text,
    ('JOIN_REQUEST'::character varying)::text
    ]));