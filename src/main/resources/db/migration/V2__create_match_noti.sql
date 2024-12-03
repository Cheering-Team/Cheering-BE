create sequence public.device_token_tb_seq
    increment by 50;

alter table public.match_tb
    add is_match_notified boolean;

alter table public.post_tb
    add community_id bigint;

create table public.device_token_tb
(
    device_token_id bigint       not null
        primary key,
    device_id       varchar(255) not null,
    token           varchar(255) not null,
    user_id         bigint
        constraint fke2w9but28wx494h0q6373sc6b
            references public.user_tb
);

alter table public.chat_room_tb
    drop constraint chat_room_tb_community_type_check;

alter table public.chat_room_tb
    add constraint chat_room_tb_community_type_check
        check ((community_type)::text = ANY
               (ARRAY [('TEAM'::character varying)::text, ('PLAYER'::character varying)::text]));

alter table public.chat_room_tb
    drop constraint chat_room_tb_type_check;

alter table public.chat_room_tb
    add constraint chat_room_tb_type_check
        check ((type)::text = ANY (ARRAY [('OFFICIAL'::character varying)::text, ('PUBLIC'::character varying)::text]));

alter table public.fan_tb
    alter column community_id drop not null;

alter table public.fan_tb
    drop constraint fan_tb_type_check;

alter table public.fan_tb
    add constraint fan_tb_type_check
        check ((type)::text = ANY
               (ARRAY [('TEAM'::character varying)::text, ('PLAYER'::character varying)::text, ('ADMIN'::character varying)::text]));

alter table public.post_image_tb
    drop constraint post_image_tb_type_check;

alter table public.post_image_tb
    add constraint post_image_tb_type_check
        check ((type)::text = ANY (ARRAY [('IMAGE'::character varying)::text, ('VIDEO'::character varying)::text]));

alter table public.user_tb
    drop constraint user_tb_role_check;

alter table public.user_tb
    add constraint user_tb_role_check
        check ((role)::text = ANY
               (ARRAY [('USER'::character varying)::text, ('ADMIN'::character varying)::text, ('PLAYER'::character varying)::text, ('TEAM'::character varying)::text]));

alter table public.apply_tb
    drop constraint apply_tb_status_check;

alter table public.apply_tb
    add constraint apply_tb_status_check
        check ((status)::text = ANY
               (ARRAY [('PENDING'::character varying)::text, ('APPROVED'::character varying)::text, ('REJECTED'::character varying)::text]));

alter table public.vote_tb
    drop constraint uk_2vbbgnt7b0xxo9ttu4hq1kuxg;