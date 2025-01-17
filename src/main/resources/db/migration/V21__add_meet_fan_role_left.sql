ALTER TABLE meet_fan_tb
DROP CONSTRAINT meet_fan_tb_role_check;

ALTER TABLE meet_fan_tb
    ADD CONSTRAINT meet_fan_tb_role_check
        CHECK ((role)::text = ANY
    (ARRAY [
    ('MANAGER'::character varying)::text,
    ('MEMBER'::character varying)::text,
    ('APPLIER'::character varying)::text,
    ('LEFT'::character varying)::text,
    ]));