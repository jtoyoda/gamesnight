CREATE TABLE user
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
    picker_id INT REFERENCES user (id)
);


CREATE TABLE user_attends_game_event
(
    id       SERIAL PRIMARY KEY,
    user_id  INT REFERENCES user (id),
    event_id INT REFERENCES game_event (id),
    attending BOOLEAN NOT NULL
);

CREATE TABLE game_night
(
  id SERIAL PRIMARY KEY ,
  name VARCHAR,
  day_of_week VARCHAR,
  repeat VARCHAR,
  hour INT,
  minute INT,
  created_on TIMESTAMP
);

CREATE TABLE user_in_game_night
(
    id SERIAL PRIMARY KEY ,
    user_id INT REFERENCES user(id),
    game_night_id INT REFERENCES game_night(id)
);
