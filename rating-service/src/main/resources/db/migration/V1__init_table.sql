
CREATE TABLE rating
(
    id                    BIGSERIAL PRIMARY KEY     NOT NULL,
    ride_id               VARCHAR(24)               NOT NULL,
    rate_for_driver       INTEGER,
    rate_for_passenger    INTEGER,
    comment_for_driver    VARCHAR(240),
    comment_for_passenger VARCHAR(240)
);

CREATE TABLE rates
(
    id          BIGSERIAL PRIMARY KEY       NOT NULL,
    person_id   VARCHAR(20)                 NOT NULL,
    rate        INTEGER                     NOT NULL
)