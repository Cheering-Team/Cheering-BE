drop sequence public.post_tag_tb_seq;

drop sequence public.tag_tb_seq;

alter table public.user_tb
    add unique (phone);

alter table public.like_tb
    add target_id bigint;

alter table public.like_tb
    add target_type varchar(255);

UPDATE public.like_tb
SET target_id = post_id;

UPDATE public.like_tb
SET target_type = 'POST';

ALTER TABLE public.like_tb
    ALTER COLUMN target_id SET NOT NULL;

ALTER TABLE public.like_tb
    ALTER COLUMN target_type SET NOT NULL;

alter table public.like_tb
drop constraint fkp67wcdups4fk9jt8xmmirqink;

alter table public.like_tb
drop column post_id;

drop table public.post_tag_tb;

drop table public.tag_tb;

alter table public.user_tb
drop constraint user_tb_phone_key1;