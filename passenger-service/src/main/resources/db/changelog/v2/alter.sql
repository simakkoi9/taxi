alter table passengers
    drop constraint if exists passengers_pk2;

alter table passengers
    add constraint passengers_pk2
        unique (email);