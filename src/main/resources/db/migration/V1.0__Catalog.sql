CREATE SEQUENCE IF NOT EXISTS roles_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS roles (
  id        INTEGER      NOT NULL CONSTRAINT roles_pk PRIMARY KEY,
  role_name VARCHAR(200) NOT NULL CONSTRAINT roles_name_ck CHECK (LENGTH(role_name) > 0),
  CONSTRAINT roles_role_name_uk UNIQUE (role_name)
);

CREATE SEQUENCE IF NOT EXISTS accounts_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS accounts (
  id       INTEGER      NOT NULL CONSTRAINT accounts_pk PRIMARY KEY,
  uuid     VARCHAR(36)  NOT NULL CONSTRAINT accounts_uuid_ck CHECK (LENGTH(uuid) > 0),
  username VARCHAR(200) NOT NULL CONSTRAINT accounts_username_ck CHECK (LENGTH(username) > 0),
  password VARCHAR(200) NOT NULL CONSTRAINT accounts_password_ck CHECK (LENGTH(password) > 0),
  locked   BOOLEAN      NOT NULL,
  CONSTRAINT accounts_uuid_uk UNIQUE (uuid),
  CONSTRAINT accounts_username_uk UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS account_roles (
  account INTEGER NOT NULL CONSTRAINT account_roles_account_fk REFERENCES accounts (id),
  role    INTEGER NOT NULL CONSTRAINT account_roles_role_fk REFERENCES roles (id)
);

CREATE SEQUENCE IF NOT EXISTS genres_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS genres (
  id                    INTEGER      NOT NULL CONSTRAINT genres_pk PRIMARY KEY,
  uuid                  VARCHAR(128) NOT NULL CONSTRAINT genres_uuid_ck CHECK (LENGTH(uuid) > 0),
  genre_name            VARCHAR(200) NOT NULL CONSTRAINT genres_genre_name_ck CHECK (LENGTH(genre_name) > 0),
  normalized_genre_name VARCHAR(200) NOT NULL CONSTRAINT genres_normalized_genre_name_ck CHECK (LENGTH(normalized_genre_name) > 0),
  created_user          VARCHAR(36)  NOT NULL,
  created_time          TIMESTAMP    NOT NULL,
  updated_user          VARCHAR(36)  NOT NULL,
  updated_time          TIMESTAMP    NOT NULL,
  CONSTRAINT genres_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_genres_normalized_genre_name ON genres(normalized_genre_name);

CREATE SEQUENCE IF NOT EXISTS pictures_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS pictures (
  id           INTEGER      NOT NULL CONSTRAINT pictures_pk PRIMARY KEY,
  uuid         VARCHAR(128) NOT NULL CONSTRAINT pictures_uuid_ck CHECK (LENGTH(uuid) > 0),
  content      BYTEA        NOT NULL,
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL,
  CONSTRAINT pictures_uuid_uk UNIQUE (uuid)
);

CREATE SEQUENCE IF NOT EXISTS registers_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS registers (
  id              INTEGER      NOT NULL CONSTRAINT registers_pk PRIMARY KEY,
  register_number INTEGER      NOT NULL CONSTRAINT registers_register_number_ck CHECK (register_number > 0),
  register_name   VARCHAR(100) NOT NULL CONSTRAINT register_values_register_name_ck CHECK (LENGTH(register_name) > 0)
);

CREATE SEQUENCE IF NOT EXISTS register_values_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS register_values (
  id             INTEGER     NOT NULL CONSTRAINT register_values_pk PRIMARY KEY,
  register       INTEGER     NOT NULL CONSTRAINT register_values_register_fk REFERENCES registers (id),
  register_code  VARCHAR(10) NOT NULL CONSTRAINT register_values_register_code_ck CHECK (LENGTH(register_code) > 0),
  register_order INTEGER     NOT NULL CONSTRAINT register_values_position_ck CHECK (register_order >= 0)
);

CREATE SEQUENCE IF NOT EXISTS media_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS media (
  id            INTEGER     NOT NULL CONSTRAINT media_pk PRIMARY KEY,
  medium_number INTEGER     NOT NULL CONSTRAINT media_medium_number_ck CHECK (medium_number > 0),
  medium_length INTEGER     NOT NULL CONSTRAINT media_medium_length_ck CHECK (medium_length >= 0),
  created_user  VARCHAR(36) NOT NULL,
  created_time  TIMESTAMP   NOT NULL,
  updated_user  VARCHAR(36) NOT NULL,
  updated_time  TIMESTAMP   NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS movies_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS movies (
  id                       INTEGER      NOT NULL CONSTRAINT movies_pk PRIMARY KEY,
  uuid                     VARCHAR(128) NOT NULL CONSTRAINT movies_uuid_ck CHECK (LENGTH(uuid) > 0),
  picture                  INTEGER      CONSTRAINT movies_picture_fk REFERENCES pictures (id),
  czech_name               VARCHAR(200) NOT NULL CONSTRAINT movies_czech_name_ck CHECK (LENGTH(czech_name) > 0),
  normalized_czech_name    VARCHAR(200) NOT NULL CONSTRAINT movies_normalized_czech_name_ck CHECK (LENGTH(normalized_czech_name) > 0),
  original_name            VARCHAR(100) NOT NULL CONSTRAINT movies_original_name_ck CHECK (LENGTH(original_name) > 0),
  normalized_original_name VARCHAR(100) NOT NULL CONSTRAINT movies_normalized_original_name_ck CHECK (LENGTH(normalized_original_name) > 0),
  movie_year               INTEGER      NOT NULL CONSTRAINT movies_movie_year_ck CHECK (movie_year BETWEEN 1930 AND 2100),
  csfd                     VARCHAR(100),
  imdb_code                INTEGER      CONSTRAINT movies_imdb_code_ck CHECK (imdb_code BETWEEN 1 AND 999999999),
  wiki_en                  VARCHAR(100),
  wiki_cz                  VARCHAR(100),
  note                     VARCHAR(100),
  created_user             VARCHAR(36)  NOT NULL,
  created_time             TIMESTAMP    NOT NULL,
  updated_user             VARCHAR(36)  NOT NULL,
  updated_time             TIMESTAMP    NOT NULL,
  CONSTRAINT movies_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_movies_normalized_czech_name ON movies(normalized_czech_name);
CREATE INDEX idx_movies_normalized_original_name ON movies(normalized_original_name);

CREATE TABLE IF NOT EXISTS movie_languages (
  movie          INTEGER     NOT NULL CONSTRAINT movie_languages_movie_fk REFERENCES movies (id),
  movie_language VARCHAR(10) NOT NULL CONSTRAINT movie_languages_movie_language_ck CHECK (LENGTH(movie_language) > 0)
);

CREATE TABLE IF NOT EXISTS movie_subtitles (
  movie     INTEGER     NOT NULL CONSTRAINT movie_subtitles_movie_fk REFERENCES movies (id),
  subtitles VARCHAR(10) NOT NULL CONSTRAINT movie_subtitles_subtitles_ck CHECK (LENGTH(subtitles) > 0)
);

CREATE TABLE IF NOT EXISTS movie_media (
  movie  INTEGER NOT NULL CONSTRAINT movie_media_movie_fk REFERENCES movies (id),
  medium INTEGER NOT NULL CONSTRAINT movie_media_medium_fk REFERENCES media (id)
);

CREATE TABLE IF NOT EXISTS movie_genres (
  movie INTEGER NOT NULL CONSTRAINT movie_genres_movie_fk REFERENCES movies (id),
  genre INTEGER NOT NULL CONSTRAINT movie_genres_genre_fk REFERENCES genres (id)
);

CREATE SEQUENCE IF NOT EXISTS tv_shows_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS tv_shows (
  id                       INTEGER      NOT NULL CONSTRAINT tv_shows_pk PRIMARY KEY,
  uuid                     VARCHAR(128) NOT NULL CONSTRAINT tv_shows_uuid_ck CHECK (LENGTH(uuid) > 0),
  picture                  INTEGER      CONSTRAINT tv_shows_picture_fk REFERENCES pictures (id),
  czech_name               VARCHAR(200) NOT NULL CONSTRAINT tv_shows_czech_name_ck CHECK (LENGTH(czech_name) > 0),
  normalized_czech_name    VARCHAR(200) NOT NULL CONSTRAINT tv_shows_normalized_czech_name_ck CHECK (LENGTH(normalized_czech_name) > 0),
  original_name            VARCHAR(100) NOT NULL CONSTRAINT tv_shows_original_name_ck CHECK (LENGTH(original_name) > 0),
  normalized_original_name VARCHAR(200) NOT NULL CONSTRAINT tv_shows_normalized_original_name_ck CHECK (LENGTH(normalized_original_name) > 0),
  csfd                     VARCHAR(100),
  imdb_code                INTEGER      CONSTRAINT tv_shows_imdb_code_ck CHECK (imdb_code BETWEEN 1 AND 999999999),
  wiki_en                  VARCHAR(100),
  wiki_cz                  VARCHAR(100),
  note                     VARCHAR(100),
  created_user             VARCHAR(36)  NOT NULL,
  created_time             TIMESTAMP    NOT NULL,
  updated_user             VARCHAR(36)  NOT NULL,
  updated_time             TIMESTAMP    NOT NULL,
  CONSTRAINT tv_shows_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_tv_shows_normalized_czech_name ON tv_shows(normalized_czech_name);
CREATE INDEX idx_tv_shows_normalized_original_name ON tv_shows(normalized_original_name);

CREATE TABLE IF NOT EXISTS tv_show_genres (
  tv_show INTEGER NOT NULL CONSTRAINT tv_show_genres_show_fk REFERENCES tv_shows (id),
  genre   INTEGER NOT NULL CONSTRAINT tv_show_genres_genre_fk REFERENCES genres (id)
);

CREATE SEQUENCE IF NOT EXISTS seasons_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS seasons (
  id              INTEGER      NOT NULL CONSTRAINT seasons_pk PRIMARY KEY,
  uuid            VARCHAR(128) NOT NULL CONSTRAINT seasons_uuid_ck CHECK (LENGTH(uuid) > 0),
  tv_show         INTEGER      CONSTRAINT seasons_tv_show_fk REFERENCES tv_shows (id),
  season_number   INTEGER      NOT NULL CONSTRAINT seasons_season_number_ck CHECK (season_number > 0),
  start_year      INTEGER      NOT NULL CONSTRAINT seasons_start_year_ck CHECK (start_year BETWEEN 1930 AND 2100),
  end_year        INTEGER      NOT NULL CONSTRAINT seasons_end_year_ck CHECK (end_year BETWEEN 1930 AND 2100),
  season_language VARCHAR(10)  NOT NULL CONSTRAINT seasons_season_language_ck CHECK (LENGTH(season_language) > 0),
  note            VARCHAR(100),
  created_user    VARCHAR(36)  NOT NULL,
  created_time    TIMESTAMP    NOT NULL,
  updated_user    VARCHAR(36)  NOT NULL,
  updated_time    TIMESTAMP    NOT NULL,
  CONSTRAINT seasons_uuid_uk UNIQUE (uuid),
  CONSTRAINT seasons_years_ck CHECK (start_year <= end_year)
);

CREATE TABLE IF NOT EXISTS season_subtitles (
  season    INTEGER     NOT NULL CONSTRAINT season_subtitles_fk REFERENCES seasons (id),
  subtitles VARCHAR(10) NOT NULL CONSTRAINT season_subtitles_subtitles_ck CHECK (LENGTH(subtitles) > 0)
);

CREATE SEQUENCE IF NOT EXISTS episodes_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS episodes (
  id             INTEGER      NOT NULL CONSTRAINT episodes_pk PRIMARY KEY,
  uuid           VARCHAR(128) NOT NULL CONSTRAINT episode_uuid_ck CHECK (LENGTH(uuid) > 0),
  season         INTEGER      CONSTRAINT episodes_season_fk REFERENCES seasons (id),
  episode_number INTEGER      NOT NULL CONSTRAINT episodes_episode_number_ck CHECK (episode_number > 0),
  episode_name   VARCHAR(100) NOT NULL CONSTRAINT episodes_episode_name_ck CHECK (LENGTH(episode_name) > 0),
  episode_length INTEGER      NOT NULL CONSTRAINT episodes_episode_length_ck CHECK (episode_length >= 0),
  note           VARCHAR(100),
  created_user   VARCHAR(36)  NOT NULL,
  created_time   TIMESTAMP    NOT NULL,
  updated_user   VARCHAR(36)  NOT NULL,
  updated_time   TIMESTAMP    NOT NULL,
  CONSTRAINT episodes_uuid_uk UNIQUE (uuid)
);

CREATE SEQUENCE IF NOT EXISTS cheats_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS cheats (
  id            INTEGER      NOT NULL CONSTRAINT cheats_pk PRIMARY KEY,
  uuid          VARCHAR(128) NOT NULL CONSTRAINT cheats_uuid_ck CHECK (LENGTH(uuid) > 0),
  game_setting  VARCHAR(200),
  cheat_setting VARCHAR(200),
  created_user  VARCHAR(36)  NOT NULL,
  created_time  TIMESTAMP    NOT NULL,
  updated_user  VARCHAR(36)  NOT NULL,
  updated_time  TIMESTAMP    NOT NULL,
  CONSTRAINT cheats_uuid_uk UNIQUE (uuid)
);

CREATE SEQUENCE IF NOT EXISTS cheat_data_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS cheat_data (
  id           INTEGER      NOT NULL CONSTRAINT cheat_data_pk PRIMARY KEY,
  action       VARCHAR(200) NOT NULL CONSTRAINT cheat_data_action_ck CHECK (LENGTH(action) > 0),
  description  VARCHAR(200) NOT NULL CONSTRAINT cheat_data_description_ck CHECK (LENGTH(description) > 0),
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS cheat_cheat_data (
  cheat      INTEGER NOT NULL CONSTRAINT cheat_cheat_data_cheat_fk REFERENCES cheats (id),
  cheat_data INTEGER NOT NULL CONSTRAINT cheat_cheat_data_cheat_data_fk REFERENCES cheat_data (id)
);

CREATE SEQUENCE IF NOT EXISTS games_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS games (
  id                   INTEGER      NOT NULL CONSTRAINT games_pk PRIMARY KEY,
  uuid                 VARCHAR(128) NOT NULL CONSTRAINT games_uuid_ck CHECK (LENGTH(uuid) > 0),
  cheat                INTEGER      CONSTRAINT games_cheat_fk REFERENCES cheats (id),
  game_name            VARCHAR(200) NOT NULL CONSTRAINT games_game_name_ck CHECK (LENGTH(game_name) > 0),
  normalized_game_name VARCHAR(200) NOT NULL CONSTRAINT games_normalized_czech_name_ck CHECK (LENGTH(normalized_game_name) > 0),
  wiki_en              VARCHAR(100),
  wiki_cz              VARCHAR(100),
  media_count          INTEGER      NOT NULL CONSTRAINT games_media_count_ck CHECK (media_count > 0),
  format               VARCHAR(10)  NOT NULL CONSTRAINT games_format_ck CHECK (LENGTH(format) > 0),
  crack                BOOLEAN      NOT NULL,
  serial_key           BOOLEAN      NOT NULL,
  patch                BOOLEAN      NOT NULL,
  trainer              BOOLEAN      NOT NULL,
  trainer_data         BOOLEAN      NOT NULL,
  editor               BOOLEAN      NOT NULL,
  saves                BOOLEAN      NOT NULL,
  other_data           VARCHAR(100),
  note                 VARCHAR(100),
  created_user         VARCHAR(36)  NOT NULL,
  created_time         TIMESTAMP    NOT NULL,
  updated_user         VARCHAR(36)  NOT NULL,
  updated_time         TIMESTAMP    NOT NULL,
  CONSTRAINT games_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_games_normalized_game_name ON games(normalized_game_name);

CREATE SEQUENCE IF NOT EXISTS music_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS music (
  id                    INTEGER      NOT NULL CONSTRAINT music_pk PRIMARY KEY,
  uuid                  VARCHAR(128) NOT NULL CONSTRAINT music_uuid_ck CHECK (LENGTH(uuid) > 0),
  music_name            VARCHAR(200) NOT NULL CONSTRAINT music_music_name_ck CHECK (LENGTH(music_name) > 0),
  normalized_music_name VARCHAR(200) NOT NULL CONSTRAINT music_normalized_music_name_ck CHECK (LENGTH(normalized_music_name) > 0),
  wiki_en               VARCHAR(100),
  wiki_cz               VARCHAR(100),
  media_count           INTEGER      NOT NULL CONSTRAINT music_media_count_ck CHECK (media_count > 0),
  note                  VARCHAR(100),
  created_user          VARCHAR(36)  NOT NULL,
  created_time          TIMESTAMP    NOT NULL,
  updated_user          VARCHAR(36)  NOT NULL,
  updated_time          TIMESTAMP    NOT NULL,
  CONSTRAINT music_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_music_normalized_music_name ON music(normalized_music_name);

CREATE SEQUENCE IF NOT EXISTS songs_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS songs (
  id           INTEGER      NOT NULL CONSTRAINT songs_pk PRIMARY KEY,
  uuid         VARCHAR(128) NOT NULL CONSTRAINT songs_uuid_ck CHECK (LENGTH(uuid) > 0),
  music        INTEGER      CONSTRAINT songs_music_fk REFERENCES music (id),
  song_name    VARCHAR(100) NOT NULL CONSTRAINT songs_song_name_ck CHECK (LENGTH(song_name) > 0),
  song_length  INTEGER      NOT NULL CONSTRAINT songs_song_length_ck CHECK (song_length >= 0),
  note         VARCHAR(100),
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL,
  CONSTRAINT songs_uuid_uk UNIQUE (uuid)
);

CREATE SEQUENCE IF NOT EXISTS programs_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS programs (
  id                      INTEGER      NOT NULL CONSTRAINT programs_pk PRIMARY KEY,
  uuid                    VARCHAR(128) NOT NULL CONSTRAINT programs_uuid_ck CHECK (LENGTH(uuid) > 0),
  program_name            VARCHAR(200) NOT NULL CONSTRAINT programs_program_name_ck CHECK (LENGTH(program_name) > 0),
  normalized_program_name VARCHAR(200) NOT NULL CONSTRAINT programs_normalized_program_name_ck CHECK (LENGTH(normalized_program_name) > 0),
  wiki_en                 VARCHAR(100),
  wiki_cz                 VARCHAR(100),
  media_count             INTEGER      NOT NULL CONSTRAINT programs_media_count_ck CHECK (media_count > 0),
  format                  VARCHAR(10)  NOT NULL CONSTRAINT programs_format_ck CHECK (LENGTH(format) > 0),
  crack                   BOOLEAN      NOT NULL,
  serial_key              BOOLEAN      NOT NULL,
  other_data              VARCHAR(100),
  note                    VARCHAR(100),
  created_user            VARCHAR(36)  NOT NULL,
  created_time            TIMESTAMP    NOT NULL,
  updated_user            VARCHAR(36)  NOT NULL,
  updated_time            TIMESTAMP    NOT NULL,
  CONSTRAINT programs_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_programs_normalized_program_name ON programs(normalized_program_name);

CREATE SEQUENCE IF NOT EXISTS authors_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS authors (
  id                     INTEGER      NOT NULL CONSTRAINT authors_pk PRIMARY KEY,
  uuid                   VARCHAR(128) NOT NULL CONSTRAINT authors_uuid_ck CHECK (LENGTH(uuid) > 0),
  first_name             VARCHAR(100) NOT NULL CONSTRAINT authors_first_name_ck CHECK (LENGTH(first_name) > 0),
  normalized_first_name  VARCHAR(100) NOT NULL CONSTRAINT authors_normalized_first_name_ck CHECK (LENGTH(normalized_first_name) > 0),
  middle_name            VARCHAR(100),
  normalized_middle_name VARCHAR(100),
  last_name              VARCHAR(100) NOT NULL CONSTRAINT authors_last_name_ck CHECK (LENGTH(last_name) > 0),
  normalized_last_name   VARCHAR(100) NOT NULL CONSTRAINT authors_normalized_last_name_ck CHECK (LENGTH(normalized_last_name) > 0),
  created_user           VARCHAR(36)  NOT NULL,
  created_time           TIMESTAMP    NOT NULL,
  updated_user           VARCHAR(36)  NOT NULL,
  updated_time           TIMESTAMP    NOT NULL,
  CONSTRAINT authors_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_authors_normalized_first_name ON authors(normalized_first_name);
CREATE INDEX idx_authors_normalized_middle_name ON authors(normalized_middle_name);
CREATE INDEX idx_authors_normalized_last_name ON authors(normalized_last_name);

CREATE SEQUENCE IF NOT EXISTS books_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS books (
  id                       INTEGER       NOT NULL CONSTRAINT books_pk PRIMARY KEY,
  uuid                     VARCHAR(128)  NOT NULL CONSTRAINT books_uuid_ck CHECK (LENGTH(uuid) > 0),
  czech_name               VARCHAR(200)  NOT NULL CONSTRAINT books_czech_name_ck CHECK (LENGTH(czech_name) > 0),
  normalized_czech_name    VARCHAR(200)  NOT NULL CONSTRAINT books_normalized_czech_name_ck CHECK (LENGTH(normalized_czech_name) > 0),
  original_name            VARCHAR(100)  NOT NULL CONSTRAINT books_original_name_ck CHECK (LENGTH(original_name) > 0),
  normalized_original_name VARCHAR(100)  NOT NULL CONSTRAINT books_normalized_original_name_ck CHECK (LENGTH(normalized_original_name) > 0),
  description              VARCHAR(1000) NOT NULL CONSTRAINT books_description_ck CHECK (LENGTH(description) > 0),
  note                     VARCHAR(100),
  created_user             VARCHAR(36)   NOT NULL,
  created_time             TIMESTAMP     NOT NULL,
  updated_user             VARCHAR(36)   NOT NULL,
  updated_time             TIMESTAMP     NOT NULL,
  CONSTRAINT books_uuid_uk UNIQUE (uuid)
);
CREATE INDEX idx_books_normalized_czech_name ON books(normalized_czech_name);
CREATE INDEX idx_books_normalized_original_name ON books(normalized_original_name);

CREATE TABLE IF NOT EXISTS book_authors (
  book   INTEGER NOT NULL CONSTRAINT book_authors_book_fk REFERENCES books (id),
  author INTEGER NOT NULL CONSTRAINT book_authors_author_fk REFERENCES authors (id)
);

CREATE SEQUENCE IF NOT EXISTS book_items_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS book_items (
  id           INTEGER      NOT NULL CONSTRAINT book_items_pk PRIMARY KEY,
  uuid         VARCHAR(128) NOT NULL CONSTRAINT book_items_uuid_ck CHECK (LENGTH(uuid) > 0),
  book         INTEGER      CONSTRAINT book_items_book_fk REFERENCES books (id),
  format       VARCHAR(10)  NOT NULL CONSTRAINT book_items_format_ck CHECK (LENGTH(format) > 0),
  note         VARCHAR(100),
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL,
  CONSTRAINT book_items_uuid_uk UNIQUE (uuid)
);

CREATE TABLE IF NOT EXISTS book_item_languages (
  book_item          INTEGER     NOT NULL CONSTRAINT book_item_languages_book_item_fk REFERENCES book_items (id),
  book_item_language VARCHAR(10) NOT NULL CONSTRAINT book_item_languages_book_item_language_ck CHECK (LENGTH(book_item_language) > 0)
);

CREATE SEQUENCE IF NOT EXISTS jokes_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS jokes (
  id           INTEGER      NOT NULL CONSTRAINT jokes_pk PRIMARY KEY,
  uuid         VARCHAR(128) NOT NULL CONSTRAINT jokes_uuid_ck CHECK (LENGTH(uuid) > 0),
  content      TEXT         NOT NULL CONSTRAINT jokes_content_ck CHECK (LENGTH(content) > 0),
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL,
  CONSTRAINT jokes_uuid_uk UNIQUE (uuid)
);

INSERT INTO roles (id, role_name) VALUES (nextval('roles_sq'), 'ROLE_ADMIN');
INSERT INTO roles (id, role_name) VALUES (nextval('roles_sq'), 'ROLE_USER');

INSERT INTO accounts (id, uuid, username, password, locked) VALUES (nextval('accounts_sq'), 'dc0d73bc-e19e-4c91-b818-192907def7ec', 'admin', '$2a$10$CKwbyaXtgmTIFJj07XGPPOR3Qn8zCNUHN97/C9tm1oEGv.hJNEJU.', false);

INSERT INTO account_roles (account, role) VALUES ((SELECT id FROM accounts WHERE uuid = 'dc0d73bc-e19e-4c91-b818-192907def7ec'), (SELECT id FROM roles WHERE role_name = 'ROLE_ADMIN'));
    
INSERT INTO registers (id, register_number, register_name) VALUES (nextval('registers_sq'), 1, 'Program formats');
INSERT INTO registers (id, register_number, register_name) VALUES (nextval('registers_sq'), 2, 'Languages');
INSERT INTO registers (id, register_number, register_name) VALUES (nextval('registers_sq'), 3, 'Subtitles');
INSERT INTO registers (id, register_number, register_name) VALUES (nextval('registers_sq'), 4, 'Book item formats');

INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 1), 'ISO', 1);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 1), 'BINARY', 2);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 1), 'STEAM', 3);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 1), 'BATTLE_NET', 4);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 2), 'CZ', 1);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 2), 'EN', 2);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 2), 'FR', 3);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 2), 'JP', 4);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 2), 'SK', 5);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 3), 'CZ', 1);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 3), 'EN', 2);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 4), 'PAPER', 1);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 4), 'PDF', 2);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 4), 'DOC', 3);
INSERT INTO register_values (id, register, register_code, register_order) VALUES (nextval('register_values_sq'), (SELECT id FROM registers WHERE register_number = 4), 'TXT', 4);
