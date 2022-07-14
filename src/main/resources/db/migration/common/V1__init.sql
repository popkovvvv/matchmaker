create table players
(
    id                bigserial primary key not null,
    name              varchar(256)          not null,
    skill             double precision      not null,
    latency           double precision      not null,
    started_search_at timestamp with time zone,
    state             jsonb                 not null default '{}',
    rank              smallint              not null
);

create unique index players_name_unique_idx ON players (name);

create table matches
(
    id                bigserial primary key not null,
    skill_statistics  jsonb                 not null default '{}',
    latency_statistic jsonb                 not null default '{}',
    time_statistic    jsonb                 not null default '{}',
    started_at        timestamp with time zone
);

create table if not exists match_players
(
    id bigserial primary key not null,
    match_id bigint references matches(id) not null,
    player_id bigint references players(id) not null,
    created_at timestamptz default now()
);

create unique index if not exists uq__matche__players on match_players(match_id, player_id);