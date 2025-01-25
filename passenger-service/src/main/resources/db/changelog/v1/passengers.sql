
CREATE TABLE IF NOT EXISTS passengers
(
    id BIGSERIAL
        CONSTRAINT passengers_pk PRIMARY KEY    NOT NULL,
    name       VARCHAR                          NOT NULL,
    email      VARCHAR                          NOT NULL,
    phone      VARCHAR                          NOT NULL,
    status     VARCHAR                          NOT NULL,
    created_at TIMESTAMP                        NOT NULL
);


