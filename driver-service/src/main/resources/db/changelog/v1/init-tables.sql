CREATE TABLE IF NOT EXISTS cars
(
    id         BIGSERIAL
        CONSTRAINT cars_pk PRIMARY KEY,
    brand      VARCHAR(25) NOT NULL,
    model      VARCHAR(40) NOT NULL,
    color      VARCHAR(40) NOT NULL,
    number     VARCHAR(15) NOT NULL,
    status     INTEGER     NOT NULL,
    created_at TIMESTAMP   NOT NULL
);

CREATE TABLE IF NOT EXISTS drivers
(
    id         BIGSERIAL
        CONSTRAINT drivers_pk PRIMARY KEY,
    name       VARCHAR(70)  NOT NULL,
    email      VARCHAR(320) NOT NULL,
    phone      VARCHAR(20)  NOT NULL,
    gender     INTEGER      NOT NULL,
    car_id     BIGINT
        CONSTRAINT drivers_cars_id_fk REFERENCES cars,
    status     INTEGER      NOT NULL,
    created_at TIMESTAMP    NOT NULL
);


