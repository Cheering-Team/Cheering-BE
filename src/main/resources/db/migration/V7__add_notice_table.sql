CREATE TABLE public.notice_tb (
    notice_id BIGINT NOT NULL PRIMARY KEY,
    title VARCHAR(256),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE
);