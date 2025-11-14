package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private static final String SQL_ALL_WITH_ID = "select id, name from genre order by id";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<Short> findIdsByFilmId(long filmId) {
        return new java.util.LinkedHashSet<>(
                jdbc.query(
                        "select genre_id from film_genre where film_id=? order by genre_id",
                        (rs, n) -> rs.getShort(1),
                        filmId
                )
        );
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

    @Override
    public void replaceForFilmByIds(long filmId, Set<Short> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            replaceForFilm(filmId, java.util.Collections.emptySet());
            return;
        }
        Set<Genre> enums = genreIds.stream()
                .map(id -> findById(id).orElseThrow(() -> new NotFoundException("Жанр не найден id=" + id)))
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        replaceForFilm(filmId, enums);
    }

    @Override
    public List<GenreDto> findAllWithIds() {
        return jdbc.query("SELECT id, name FROM genre ORDER BY id", (rs, rowNum) -> {
            String code = rs.getString("name");
            Genre g = Genre.valueOf(code);
            return new GenreDto(rs.getShort("id"), g.getDisplayName());
        });
    }

    @Override
    public Optional<String> nameById(short id) {
        return jdbc.query("SELECT name FROM genre WHERE id=?", rs -> {
            if (!rs.next()) return Optional.empty();
            String code = rs.getString("name");
            return Optional.of(Genre.valueOf(code).getDisplayName());
        }, id);
    }
}