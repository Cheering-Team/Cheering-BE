drop sequence public.post_tag_tb_seq;

drop sequence public.tag_tb_seq;

alter table public.user_tb
    add unique (phone);

alter table public.like_tb
    add target_id bigint not null;

alter table public.like_tb
    add target_type varchar(255) not null;

UPDATE public.like_tb
SET target_id = post_id;

alter table public.like_tb
drop constraint fkp67wcdups4fk9jt8xmmirqink;

alter table public.like_tb
drop column post_id;

drop table public.post_tag_tb;

drop table public.tag_tb;

drop schema pg_catalog;

alter table public.user_tb
drop constraint user_tb_phone_key1;