package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String SQL_ALL = "select name from genre order by id";
    private static final String SQL_BY_ID = "select name from genre where id=?";
    private static final String SQL_ID_OF = "select id from genre where name=?";
    private static final String SQL_BY_FILM = """
            select g.name from film_genre fg
            join genre g on g.id=fg.genre_id
            where fg.film_id=? order by g.id
            """;
    private static final String SQL_DELETE_FOR_FILM = "delete from film_genre where film_id=?";
    private static final String SQL_INSERT_LINK = "insert into film_genre(film_id,genre_id) values(?,?)";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(SQL_ALL);
    }

    @Override
    public Optional<Genre> findById(short id) {
        return findOne(SQL_BY_ID, id);
    }

    @Override
    public Optional<Short> idOf(Genre genre) {
        return jdbc.query(SQL_ID_OF, rs -> rs.next() ? Optional.of(rs.getShort(1)) : Optional.empty(), genre.name());
    }

    @Override
    public Set<Genre> findByFilmId(long filmId) {
        return new LinkedHashSet<>(findMany(SQL_BY_FILM, filmId));
    }

    @Override
    public void replaceForFilm(long filmId, Set<Genre> genres) {
        jdbc.update(SQL_DELETE_FOR_FILM, filmId);
        if (genres == null || genres.isEmpty()) return;
        for (Genre g : genres) {
            short gid = idOf(g).orElseThrow();
            jdbc.update(SQL_INSERT_LINK, filmId, gid);
        }
    }
}