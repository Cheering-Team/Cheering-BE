
ALTER TABLE chat_tb DROP CONSTRAINT chat_tb_type_check;

ALTER TABLE chat_tb
ADD CONSTRAINT chat_tb_type_check
CHECK (
    (type)::text = ANY (
        ARRAY[
            ('MESSAGE'::character varying)::text,
            ('SYSTEM_ENTER'::character varying)::text,
            ('SYSTEM_EXIT'::character varying)::text,
            ('JOIN_REQUEST'::character varying)::text
        ]
    )
);