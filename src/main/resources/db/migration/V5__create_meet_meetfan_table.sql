create sequence meet_tb_seq
    increment by 50;

alter table chat_room_tb
    add meet_id bigint;

alter table match_tb
    add constraint match_tb_status_check
        check ((status)::text = ANY
    ((ARRAY ['not_started'::character varying, 'started'::character varying, 'live'::character varying, 'postponed'::character varying, 'suspended'::character varying, 'match_about_to_start'::character varying, 'delayed'::character varying, 'interrupted'::character varying, 'cancelled'::character varying, 'ended'::character varying, 'closed'::character varying])::text[]));

create table meet_tb
(
    meet_id        bigint       not null
        primary key,
    created_at     timestamp(6),
    updated_at     timestamp(6),
    age_max        integer,
    age_min        integer,
    community_id   bigint       not null,
    community_type varchar(255) not null
        constraint meet_tb_community_type_check
            check ((community_type)::text = ANY
        ((ARRAY ['TEAM'::character varying, 'PLAYER'::character varying, 'ADMIN'::character varying])::text[])),
    description    varchar(255),
    gender         varchar(255) not null
        constraint meet_tb_gender_check
            check ((gender)::text = ANY
                   ((ARRAY ['MALE'::character varying, 'FEMALE'::character varying, 'ANY'::character varying])::text[])),
    has_ticket     boolean      not null,
    max            integer      not null,
    place          varchar(255),
    time           timestamp(6),
    title          varchar(255) not null,
    type           smallint     not null
        constraint meet_tb_type_check
            check ((type >= 0) AND (type <= 2)),
    chat_room_id   bigint
        constraint fk5p8o7blflj85jguguki93su3o
            references chat_room_tb,
    match_id       bigint
        constraint fkcp72jn4safd9lossud2797v85
            references match_tb
);

alter table chat_room_tb
    add constraint fk_chat_room_meet
        foreign key (meet_id) references meet_tb;

create table meet_fan_tb
(
    meet_fan_id bigserial
        primary key,
    role        varchar(255) not null
        constraint meet_fan_tb_role_check
            check ((role)::text = ANY ((ARRAY ['MANAGER'::character varying, 'MEMBER'::character varying])::text[])),
    fan_id      bigint       not null
        constraint fkpoap1p3xg38odye9afs1jgghi
            references fan_tb,
    meet_id     bigint       not null
        constraint fkljmbkayk5pvv1dqq2xmlrrb39
            references meet_tb,
    created_at  timestamp(6),
    updated_at  timestamp(6)
);


alter table chat_room_tb
drop constraint chat_room_tb_community_type_check;

alter table chat_room_tb
    add constraint chat_room_tb_community_type_check
        check ((community_type)::text = ANY
    ((ARRAY ['TEAM'::character varying, 'PLAYER'::character varying, 'ADMIN'::character varying])::text[]));

alter table chat_room_tb
drop constraint chat_room_tb_type_check;

alter table chat_room_tb
    add constraint chat_room_tb_type_check
        check ((type)::text = ANY
    ((ARRAY ['OFFICIAL'::character varying, 'PUBLIC'::character varying, 'PRIVATE'::character varying, 'CONFIRM'::character varying])::text[]));