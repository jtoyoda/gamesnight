CREATE TABLE gamer
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR,
    email    VARCHAR UNIQUE,
    password VARCHAR,
    token    VARCHAR UNIQUE
);



CREATE TABLE game_event
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR,
    game      VARCHAR,
    date      TIMESTAMP,
    picker_id INT REFERENCES gamer (id)
);


CREATE TABLE gamer_attends_game_event
(
    id        SERIAL PRIMARY KEY,
    gamer_id   INT REFERENCES gamer (id),
    event_id  INT REFERENCES game_event (id),
    attending BOOLEAN NOT NULL
);

CREATE TABLE game_night
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR,
    day_of_week VARCHAR,
    repeat      VARCHAR,
    hour        INT,
    minute      INT,
    created_on  TIMESTAMP
);

CREATE TABLE gamer_in_game_night
(
    id            SERIAL PRIMARY KEY,
    gamer_id       INT REFERENCES gamer (id),
    game_night_id INT REFERENCES game_night (id)
);
