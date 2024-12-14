drop sequence public.message_tb_seq;

alter table public.user_tb
    add constraint uk_jjhogmvl1sxad3l1018wkl5wr
        unique (phone);

alter table public.user_tb
    add unique (phone);

alter table public.chat_session_tb
    add created_at timestamp(6);

alter table public.chat_session_tb
    add updated_at timestamp(6);

alter table public.chat_tb
    add content varchar(255) not null;

alter table public.chat_tb
    add group_key varchar(255) not null;

alter table public.chat_tb
    add type varchar(255) not null;

create index idx_room_group
    on public.chat_tb (chat_room_id, group_key);

alter table public.chat_tb
    add constraint chat_tb_type_check
        check ((type)::text = ANY
    (ARRAY [('MESSAGE'::character varying)::text, ('SYSTEM_ENTER'::character varying)::text, ('SYSTEM_EXIT'::character varying)::text]));

drop table public.message_tb;

alter table public.user_tb
drop column device_token;

alter table public.user_tb
drop constraint user_tb_phone_key;