alter table player_tb drop column type;

alter table player_tb add column team_id BIGINT;

alter table player_tb add constraint fk_team foreign key (team_id) references team_tb(team_id) on delete set null;