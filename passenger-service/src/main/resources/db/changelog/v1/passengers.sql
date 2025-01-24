
drop type if exists user_status;
create type user_status as enum ('ACTIVE', 'DELETED');

create table if not exists passengers
(
    id bigserial
        constraint passengers_pk primary key                    not null,
    name       varchar(255)                                     not null,
    email      varchar(255)                                     not null,
    phone      varchar(255)                                     not null,
    status     user_status    default 'ACTIVE'::user_status     not null,
    created_at timestamp default now()                          not null
);


