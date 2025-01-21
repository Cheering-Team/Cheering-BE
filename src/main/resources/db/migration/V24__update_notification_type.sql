ALTER TABLE notification_tb
    DROP CONSTRAINT notification_tb_type_check;

ALTER TABLE notification_tb
    ADD CONSTRAINT notification_tb_type_check
        CHECK ((type)::text = ANY
    (ARRAY [
    ('LIKE'::character varying)::text,
    ('COMMNET'::character varying)::text,
    ('RECOMMNET'::character varying)::text,
    ('MEET_NEW_APPLY'::character varying)::text,
    ('MEET_DELETE'::character varying)::text,
    ('MEET_JOIN_ACCEPT'::character varying)::text,
    ('MEET_JOIN_REQUEST'::character varying)::text
    ]));