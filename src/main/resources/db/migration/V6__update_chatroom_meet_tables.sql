alter table meet_tb
drop constraint meet_tb_type_check;

alter table meet_tb
alter column type type varchar(255) using type::varchar;

alter table meet_tb
    add constraint meet_tb_type_check
        check (type in ('LIVE', 'BOOKING'));

alter table meet_tb
    alter column has_ticket drop not null;