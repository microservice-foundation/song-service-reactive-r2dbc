INSERT INTO SONG(id, resource_id, name, artist, album, length, year, created_date)
VALUES(nextval('song_sequence'), 1, 'Clean Code', 'Bob Martin', 'Software Engineering', '12:56', 2000, '2022-10-10T19:00');

INSERT INTO SONG(id, resource_id, name, artist, album, length, year, created_date, last_modified_date)
VALUES(nextval('song_sequence'), 2, 'Mess Code', 'Anton Nicola', 'Software Engineering', '1:34', 2022, '2022-10-02T10:00', '2022-10-12T17:00');

INSERT INTO SONG(id, resource_id, name, artist, album, length, year, created_date)
VALUES(nextval('song_sequence'), 3, 'Tough Code', 'John Neng', 'Software Engineering', '06:54', 2009, '2021-02-10T19:00');

INSERT INTO SONG(id, resource_id, name, artist, album, length, year, created_date, last_modified_date)
VALUES(nextval('song_sequence'), 4, 'Dirty Code', 'Sam Peter', 'Software Engineering', '09:33', 2003, '2008-10-20T19:00', '2021-11-20T19:00');

INSERT INTO SONG(id, resource_id, name, artist, album, length, year, created_date)
VALUES(nextval('song_sequence'), 5, 'Pure Code', 'Tom Ashley', 'Software Engineering', '13:56', 2010, '2020-10-10T19:00');