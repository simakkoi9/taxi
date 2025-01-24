
create table if not exists passengers
(
    id bigserial
        constraint passengers_pk primary key    not null,
    name       varchar                          not null,
    email      varchar                          not null,
    phone      varchar                          not null,
    status     varchar                          not null,
    created_at timestamp                        not null
);


