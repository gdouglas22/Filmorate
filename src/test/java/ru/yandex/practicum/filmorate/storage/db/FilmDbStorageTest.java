// src/test/java/ru/yandex/practicum/filmorate/storage/db/FilmDbStorageTest.java
package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper.class,
        MpaDbStorage.class, ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper.class,
        FilmLikeDbStorage.class
})
@Sql(scripts = {"/schema.sql", "/sql/test-genres.sql", "/sql/test-mpa.sql", "/sql/test-users.sql"})
class FilmDbStorageTest {

    @Autowired FilmDbStorage films;
    @Autowired GenreDbStorage genresStorage;
    @Autowired MpaDbStorage mpa;
    @Autowired FilmLikeDbStorage likes;

    @Test
    void create_find_update_withGenres_andLikes() {
        Film f = new Film();
        f.setName("Matrix");
        f.setDescription("Neo");
        f.setReleaseDate(LocalDate.of(1999,3,31));
        f.setDuration(136);
        f.setMpaRating(MpaRating.R);
        f.getGenres().add(Genre.Триллер);
        f.getGenres().add(Genre.Боевик);

        var created = films.create(f);
        assertThat(created.getId()).isPositive();

        // жанры привязались
        var reloaded = films.findById(created.getId()).orElseThrow();
        assertThat(reloaded.getGenres()).containsExactly(Genre.Триллер, Genre.Боевик);

        // лайки
        likes.add(created.getId(), 1L);
        likes.add(created.getId(), 2L);
        assertThat(likes.userIds(created.getId())).containsExactly(1L, 2L);

        // обновление
        reloaded.setDescription("Updated");
        reloaded.getGenres().clear();
        reloaded.getGenres().add(Genre.Драма);
        var upd = films.update(reloaded);

        var reloaded2 = films.findById(created.getId()).orElseThrow();
        assertThat(reloaded2.getDescription()).isEqualTo("Updated");
        assertThat(reloaded2.getGenres()).containsExactly(Genre.Драма);
    }
}
