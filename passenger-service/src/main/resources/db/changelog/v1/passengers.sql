
CREATE TABLE IF NOT EXISTS passengers
(
    id BIGSERIAL
        CONSTRAINT passengers_pk PRIMARY KEY     NOT NULL,
    external_id VARCHAR(255)                     NOT NULL,
    name        VARCHAR(70)                      NOT NULL,
    email       VARCHAR(320)                     NOT NULL,
    phone       VARCHAR(20)                      NOT NULL,
    status      INTEGER                          NOT NULL,
    created_at  TIMESTAMP                        NOT NULL
);


