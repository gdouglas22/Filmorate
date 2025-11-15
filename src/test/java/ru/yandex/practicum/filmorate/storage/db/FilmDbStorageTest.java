package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Import({FilmDbStorage.class, FilmRowMapper.class, MpaDbStorage.class, MpaRowMapper.class, GenreDbStorage.class, GenreRowMapper.class, FilmLikeDbStorage.class})
class FilmDbStorageTest extends AbstractDbStorageTest {

    @Autowired
    FilmDbStorage filmStorage;

    @Test
    @DisplayName("findById возвращает фильм с жанрами и лайками")
    void findById_populatesGenresAndLikes() {
        Film film = filmStorage.findById(1).orElseThrow();

        assertEquals("Inception", film.getName());
        assertEquals(Set.of(Genre.THRILLER, Genre.ACTION), film.getGenres());
        assertEquals(Set.of(1L, 2L), film.getLikes());
    }

    @Test
    @DisplayName("create сохраняет фильм с жанрами и лайками")
    void create_persistsFilm() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Space travel epic");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);
        film.setMpaRating(MpaRating.PG_13);
        film.getGenres().add(Genre.DRAMA);
        film.getGenres().add(Genre.ACTION);
        film.getLikes().add(1L);
        film.getLikes().add(3L);

        Film saved = filmStorage.create(film);

        assertTrue(saved.getId() > 0);
        assertEquals("Interstellar", saved.getName());
        assertEquals(Set.of(Genre.DRAMA, Genre.ACTION), saved.getGenres());
        assertEquals(Set.of(1L, 3L), saved.getLikes());

        Film reloaded = filmStorage.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getGenres(), reloaded.getGenres());
        assertEquals(saved.getLikes(), reloaded.getLikes());
    }

    @Test
    @DisplayName("update изменяет данные и заменяет жанры")
    void update_replacesGenres() {
        Film film = filmStorage.findById(2).orElseThrow();
        film.setName("Finding Nemo Updated");
        film.setDuration(105);
        film.getGenres().clear();
        film.getGenres().add(Genre.DOCUMENTARY);

        Film updated = filmStorage.update(film);

        assertEquals("Finding Nemo Updated", updated.getName());
        assertEquals(105, updated.getDuration());
        assertEquals(Set.of(Genre.DOCUMENTARY), updated.getGenres());

        Film reloaded = filmStorage.findById(film.getId()).orElseThrow();
        assertEquals(Set.of(Genre.DOCUMENTARY), reloaded.getGenres());
    }
}
