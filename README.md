# java-filmorate
Template repository for Filmorate project.

## База данных
# 📘 Пояснение к схеме базы данных Filmorate

Схема базы данных поддерживает хранение фильмов, пользователей, их взаимодействий (лайки, дружба), а также справочников жанров и рейтингов **MPA**.  
Все таблицы спроектированы с соблюдением принципов **нормализации (1NF–3NF)**:  
- каждое поле хранит только одно значение;  
- отсутствует дублирование данных;  
- неключевые атрибуты зависят только от первичного ключа.  

## 🔹 Основные таблицы

- **`users`** — хранит данные о пользователях: e-mail, логин, имя, дату рождения.  
- **`films`** — содержит информацию о фильмах, включая описание, дату релиза, длительность и рейтинг MPA.  
- **`genre`** и **`mpa_rating`** — справочники жанров и возрастных рейтингов.  
- **`film_genre`** — связывает фильмы и жанры (отношение «многие ко многим»).  
- **`film_like`** — хранит лайки пользователей к фильмам (уникальная пара `film_id + user_id`).  
- **`friendship`** — реализует дружбу между пользователями со статусом:  
  - `UNCONFIRMED` — запрос отправлен;  
  - `CONFIRMED` — дружба подтверждена.  

## 🧠 Примеры SQL-запросов

### Получение всех фильмов с жанрами и рейтингом
```sql
SELECT f.id,
       f.name,
       f.description,
       mr.code AS mpa_rating,
       ARRAY_AGG(g.name ORDER BY g.name) AS genres
FROM films f
JOIN mpa_rating mr ON mr.id = f.mpa_rating_id
LEFT JOIN film_genre fg ON fg.film_id = f.id
LEFT JOIN genre g ON g.id = fg.genre_id
GROUP BY f.id, mr.code
ORDER BY f.id;
```
### Топ-10 популярных фильмов по лайкам
```sql
SELECT f.id, f.name, COUNT(fl.user_id) AS likes
FROM films f
LEFT JOIN film_like fl ON fl.film_id = f.id
GROUP BY f.id, f.name
ORDER BY likes DESC
LIMIT 10;
```
### Добавление нового фильма
```sql
INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
VALUES ('Inception', 'Sci-fi thriller', '2010-07-16', 148, 4)
RETURNING id;
```
### Добавление жанров к фильму
```sql
INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 2), (1, 4);
```
### Пользователь ставит лайк фильму
```sql
INSERT INTO film_like (film_id, user_id)
VALUES (5, 12)
ON CONFLICT (film_id, user_id) DO NOTHING;
```
### Отправка и подтверждение дружбы
```sql
WITH confirmed AS (
  SELECT requester_id AS u, addressee_id AS f
  FROM friendship WHERE status = 'CONFIRMED'
  UNION
  SELECT addressee_id AS u, requester_id AS f
  FROM friendship WHERE status = 'CONFIRMED'
)
SELECT u2.*
FROM confirmed cf1
JOIN confirmed cf2 ON cf1.f = cf2.f
JOIN users u2 ON u2.id = cf1.f
WHERE cf1.u = 1 AND cf2.u = 3;
```
