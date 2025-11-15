package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Profile("db")
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String SQL_FIND_ALL = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   m.code AS mpa_code
              FROM films f
              JOIN mpa_rating m ON f.mpa_rating_id = m.id
              ORDER BY f.id
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   m.code AS mpa_code
              FROM films f
              JOIN mpa_rating m ON f.mpa_rating_id = m.id
             WHERE f.id = ?
            """;

    private static final String SQL_INSERT = """
            INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE films
               SET name = ?,
                   description = ?,
                   release_date = ?,
                   duration = ?,
                   mpa_rating_id = ?
             WHERE id = ?
            """;

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmLikeStorage likeStorage;

    public FilmDbStorage(JdbcTemplate jdbc,
                        FilmRowMapper mapper,
                        @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
                        @Qualifier("genreDbStorage") GenreStorage genreStorage,
                        @Qualifier("filmLikeDbStorage") FilmLikeStorage likeStorage) {
        super(jdbc, mapper);
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public Collection<Film> findAll() {
        var list = findMany(SQL_FIND_ALL);
        list.forEach(this::fillCollections);
        return list;
    }

    @Override
    public Optional<Film> findById(long id) {
        var opt = findOne(SQL_FIND_BY_ID, id);
        opt.ifPresent(this::fillCollections);
        return opt;
    }

    @Override
    public Film create(Film film) {
        short mpaId = mpaStorage.idOf(film.getMpaRating())
                .orElseThrow(() -> new IllegalStateException("Не найден id для MPA " + film.getMpaRating()));

        long id = insert(SQL_INSERT,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaId);

        film.setId(id);
        genreStorage.replaceForFilm(id, film.getGenres());
        film.getLikes().forEach(u -> likeStorage.add(id, u));
        return findById(id).orElse(film);
    }

    @Override
    public Film update(Film film) {
        short mpaId = mpaStorage.idOf(film.getMpaRating())
                .orElseThrow(() -> new IllegalStateException("Не найден id для MPA " + film.getMpaRating()));

        int n = update(SQL_UPDATE,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaId,
                film.getId());
        if (n == 0) throw new IllegalStateException("Film not updated id=" + film.getId());

        genreStorage.replaceForFilm(film.getId(), film.getGenres());
        return findById(film.getId()).orElse(film);
    }

    private void fillCollections(Film f) {
        f.getGenres().clear();
        f.getGenres().addAll(genreStorage.findByFilmId(f.getId()));
        f.getLikes().clear();
        f.getLikes().addAll(likeStorage.userIds(f.getId()));
    }
}
