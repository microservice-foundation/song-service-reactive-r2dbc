DROP TABLE IF EXISTS SONGS CASCADE;
DROP SEQUENCE IF EXISTS SONGS_SEQUENCE CASCADE;
CREATE SEQUENCE IF NOT EXISTS SONGS_SEQUENCE as bigint;

CREATE TABLE IF NOT EXISTS SONGS(
    "id" bigint PRIMARY KEY DEFAULT nextval('SONGS_SEQUENCE'),
    "resource_id" bigint NOT NULL UNIQUE,
    "name" varchar(100) NOT NULL,
    "artist" varchar(50),
    "album" varchar(50),
    "length" varchar(10) NOT NULL,
    "year" integer,
    "last_modified_date" TIMESTAMP,
    "created_date" TIMESTAMP NOT NULL
);

ALTER SEQUENCE SONGS_SEQUENCE OWNED BY SONGS."id";