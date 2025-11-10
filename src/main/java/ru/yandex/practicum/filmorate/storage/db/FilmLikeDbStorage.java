package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.util.LinkedHashSet;
import java.util.Set;

@Repository
@Qualifier("filmLikeDbStorage")
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbc;

    public FilmLikeDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void add(long filmId, long userId) {
        jdbc.update("merge into film_like(film_id,user_id) key(film_id,user_id) values(?,?)", filmId, userId);
    }

    @Override
    public void remove(long filmId, long userId) {
        jdbc.update("delete from film_like where film_id=? and user_id=?", filmId, userId);
    }

    @Override
    public Set<Long> userIds(long filmId) {
        return new LinkedHashSet<>(jdbc.query(
                "select user_id from film_like where film_id=? order by user_id",
                (rs, n) -> rs.getLong(1), filmId));
    }
}
