CREATE TABLE IF NOT EXISTS cars
(
    id         BIGSERIAL CONSTRAINT cars_pk     PRIMARY KEY,
    brand      VARCHAR                          NOT NULL,
    model      VARCHAR                          NOT NULL,
    color      VARCHAR                          NOT NULL,
    number     VARCHAR                          NOT NULL,
    status     INTEGER                          NOT NULL,
    created_at TIMESTAMP                        NOT NULL
);

CREATE TABLE IF NOT EXISTS drivers
(
    id          BIGSERIAL CONSTRAINT drivers_pk      PRIMARY KEY,
    name        VARCHAR                              NOT NULL,
    email       VARCHAR                              NOT NULL,
    phone       VARCHAR                              NOT NULL,
    gender      INTEGER                              NOT NULL,
    car_id      BIGINT CONSTRAINT drivers_cars_id_fk REFERENCES cars,
    status      INTEGER                              NOT NULL,
    created_at  TIMESTAMP                            NOT NULL
);


