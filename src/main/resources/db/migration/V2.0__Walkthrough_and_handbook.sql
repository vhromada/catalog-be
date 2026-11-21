CREATE SEQUENCE IF NOT EXISTS walkthroughs_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS walkthroughs (
  id           INTEGER      NOT NULL CONSTRAINT walkthroughs_pk PRIMARY KEY,
  uuid         VARCHAR(128) NOT NULL CONSTRAINT walkthroughs_uuid_ck CHECK (LENGTH(uuid) > 0),
  content      TEXT         NOT NULL CONSTRAINT walkthroughs_content_ck CHECK (LENGTH(content) > 0),
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL,
  CONSTRAINT walkthroughs_uuid_uk UNIQUE (uuid)
);

ALTER TABLE games ADD COLUMN walkthrough INTEGER CONSTRAINT games_walkthrough_fk REFERENCES walkthroughs (id);

CREATE SEQUENCE IF NOT EXISTS handbooks_sq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS handbooks (
  id           INTEGER      NOT NULL CONSTRAINT handbooks_pk PRIMARY KEY,
  uuid         VARCHAR(128) NOT NULL CONSTRAINT handbooks_uuid_ck CHECK (LENGTH(uuid) > 0),
  content      TEXT         NOT NULL CONSTRAINT handbooks_content_ck CHECK (LENGTH(content) > 0),
  created_user VARCHAR(36)  NOT NULL,
  created_time TIMESTAMP    NOT NULL,
  updated_user VARCHAR(36)  NOT NULL,
  updated_time TIMESTAMP    NOT NULL,
  CONSTRAINT handbooks_uuid_uk UNIQUE (uuid)
);

ALTER TABLE games ADD COLUMN handbook INTEGER CONSTRAINT games_handbook_fk REFERENCES handbooks (id);
