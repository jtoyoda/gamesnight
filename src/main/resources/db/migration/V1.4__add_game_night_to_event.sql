ALTER TABLE game_event ADD COLUMN game_night_id INT REFERENCES game_night (id);
