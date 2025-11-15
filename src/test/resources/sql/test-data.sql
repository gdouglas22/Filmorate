set referential_integrity false;

truncate table film_like;
truncate table friendship;
truncate table film_genre;
truncate table films;
truncate table genre;
truncate table mpa_rating;
truncate table users;

alter table users alter column id restart with 1;
alter table films alter column id restart with 1;

set referential_integrity true;

insert into mpa_rating (id, code) values
 (1, 'G'),
 (2, 'PG'),
 (3, 'PG_13'),
 (4, 'R'),
 (5, 'NC_17');

insert into genre (id, name) values
 (1, 'COMEDY'),
 (2, 'DRAMA'),
 (3, 'CARTOON'),
 (4, 'THRILLER'),
 (5, 'DOCUMENTARY'),
 (6, 'ACTION');

insert into users (email, login, name, birthday) values
 ('alice@example.com', 'alice', 'Alice', '1995-03-14'),
 ('bob@example.com',   'bob',   'Bob',   '1990-07-22'),
 ('carol@example.com', 'carol', 'Carol', '1988-11-05'),
 ('dave@example.com',  'dave',  'Dave',  '2000-01-17');

insert into films (name, description, release_date, duration, mpa_rating_id) values
 ('Inception',        'Mind-bending thriller about dreams within dreams', '2010-07-16', 148, 4),
 ('Finding Nemo',     'Animated underwater adventure about family and friendship', '2003-05-30', 100, 1),
 ('The Matrix',       'Action sci-fi about simulated reality', '1999-03-31', 136, 4),
 ('The Mask',         'Comedy with Jim Carrey and a magical mask', '1994-07-29', 101, 2),
 ('The Social Network','Drama about the creation of Facebook', '2010-10-01', 120, 3);

insert into film_genre (film_id, genre_id) values
 (1, 4),
 (1, 6),
 (2, 3),
 (3, 6),
 (3, 4),
 (4, 1),
 (5, 2);

insert into film_like (film_id, user_id) values
 (1, 1),
 (1, 2),
 (2, 1),
 (3, 3),
 (4, 2),
 (4, 4),
 (5, 1),
 (5, 3);

insert into friendship (requester_id, addressee_id, status) values
 (1, 2, 'CONFIRMED'),
 (2, 3, 'UNCONFIRMED'),
 (3, 4, 'CONFIRMED'),
 (4, 1, 'UNCONFIRMED');