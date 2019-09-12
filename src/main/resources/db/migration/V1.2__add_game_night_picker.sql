CREATE TABLE game_night_picker
(
    id            SERIAL PRIMARY KEY,
    gamer_id      INT    NOT NULL REFERENCES gamer (id) ON DELETE CASCADE,
    game_night_id INT    NOT NULL REFERENCES game_night (id) ON DELETE CASCADE,
    week          BIGINT NOT NULL
);
