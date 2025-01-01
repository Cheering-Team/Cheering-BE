create sequence meet_tb_seq
    increment by 50;

alter table chat_room_tb
    add meet_id bigint;

alter table match_tb
    add constraint match_tb_status_check
        check ((status)::text = ANY
    (ARRAY [('not_started'::character varying)::text, ('started'::character varying)::text, ('live'::character varying)::text, ('postponed'::character varying)::text, ('suspended'::character varying)::text, ('match_about_to_start'::character varying)::text, ('delayed'::character varying)::text, ('interrupted'::character varying)::text, ('cancelled'::character varying)::text, ('ended'::character varying)::text, ('closed'::character varying)::text]));

create table meet_tb
(
    meet_id              bigint       not null
        primary key,
    created_at           timestamp(6),
    updated_at           timestamp(6),
    age_max              integer,
    age_min              integer,
    community_id         bigint       not null,
    community_type       varchar(255) not null
        constraint meet_tb_community_type_check
            check ((community_type)::text = ANY
        ((ARRAY ['TEAM'::character varying, 'PLAYER'::character varying, 'ADMIN'::character varying])::text[])),
    description          varchar(255),
    gender               varchar(255) not null
        constraint meet_tb_gender_check
            check ((gender)::text = ANY
                   ((ARRAY ['MALE'::character varying, 'FEMALE'::character varying, 'ANY'::character varying])::text[])),
    has_ticket           boolean,
    max                  integer      not null,
    place                varchar(255),
    title                varchar(255) not null,
    type                 smallint     not null
        constraint meet_tb_type_check
            check ((type >= 0) AND (type <= 1)),
    confirm_chat_room_id bigint
        constraint uk_4uca3l0ixxl9yx202ml9o7anu
            unique
        constraint fkj40e23jj92r9s89gqgktxexua
            references chat_room_tb,
    match_id             bigint
        constraint fkcp72jn4safd9lossud2797v85
            references match_tb
);

alter table chat_room_tb
    add constraint fkf85bawqoj0y5gbhaep5fos1f9
        foreign key (meet_id) references meet_tb;

create table meet_fan_tb
(
    meet_fan_id bigserial
        primary key,
    created_at  timestamp(6),
    updated_at  timestamp(6),
    role        varchar(255) not null
        constraint meet_fan_tb_role_check
            check ((role)::text = ANY ((ARRAY ['MANAGER'::character varying, 'MEMBER'::character varying])::text[])),
    fan_id      bigint       not null
        constraint fkpoap1p3xg38odye9afs1jgghi
            references fan_tb,
    meet_id     bigint       not null
        constraint fkljmbkayk5pvv1dqq2xmlrrb39
            references meet_tb
);

alter table meet_fan_tb
    owner to postgre;

alter table chat_room_tb
drop constraint chat_room_tb_community_type_check;

alter table chat_room_tb
    add constraint chat_room_tb_community_type_check
        check ((community_type)::text = ANY
    (ARRAY [('TEAM'::character varying)::text, ('PLAYER'::character varying)::text, ('ADMIN'::character varying)::text]));

alter table chat_room_tb
drop constraint chat_room_tb_type_check;

alter table chat_room_tb
    add constraint chat_room_tb_type_check
        check ((type)::text = ANY
    (ARRAY [('OFFICIAL'::character varying)::text, ('PUBLIC'::character varying)::text, ('PRIVATE'::character varying)::text, ('CONFIRM'::character varying)::text]));

