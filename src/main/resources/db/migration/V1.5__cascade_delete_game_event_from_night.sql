ALTER TABLE game_event DROP CONSTRAINT game_event_game_night_id_fkey;
ALTER TABLE game_event ADD CONSTRAINT game_event_game_night_id_fkey
        FOREIGN KEY (game_night_id) REFERENCES game_night (id) ON DELETE CASCADE;
