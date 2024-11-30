create sequence public.fan_vote_tb_seq
    increment by 50;

create sequence public.vote_option_tb_seq
    increment by 50;

create sequence public.vote_tb_seq
    increment by 50;

create table public.vote_tb
(
    vote_id    bigint       not null
        primary key,
    created_at timestamp(6),
    updated_at timestamp(6),
    end_time   timestamp(6) not null,
    title      varchar(50)  not null,
    match_id   bigint
        constraint uk_2vbbgnt7b0xxo9ttu4hq1kuxg
            unique
        constraint fk71u8c2una8li3ob77cxbcl324
            references public.match_tb,
    post_id    bigint       not null
        constraint uk_r8vhebo3a3hypg18bm4aajosy
            unique
        constraint fk929676hqsnfbcmjm9sl4o4lny
            references public.post_tb
);

create table public.vote_option_tb
(
    vote_option_id   bigint       not null
        primary key,
    community_id     bigint,
    image            varchar(255),
    name             varchar(255) not null,
    vote_id          bigint       not null
        constraint fkemyioad49km8xj11gtep1dyu2
            references public.vote_tb,
    background_image varchar(255)
);

create table public.fan_vote_tb
(
    fan_vote_id    bigint not null
        primary key,
    fan_id         bigint not null
        constraint fkcaoskks7w4sbee1cwnebfb9ij
            references public.fan_tb,
    vote_id        bigint not null
        constraint fk4u1l5l7r603shyiifk6io7ta8
            references public.vote_tb,
    vote_option_id bigint not null
        constraint fk4qk5kd0006oc9w2py9asawbm7
            references public.vote_option_tb
);