package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    private Film validFilm() {
        Film f = new Film();
        f.setName("Test Film");
        f.setDescription("Описание");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(Duration.ofMinutes(90));
        return f;
    }

    @Test
    void createFilm_ok_withValidData() {
        Film saved = controller.createFilm(validFilm());
        assertEquals("Test Film", saved.getName());
    }

    @Test
    void createFilm_fails_whenNameBlank() {
        Film f = validFilm();
        f.setName("  ");
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }


    @Test
    void createFilm_fails_whenDescriptionBlank() {
        Film f = validFilm();
        f.setDescription(null);
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }

    @Test
    void createFilm_fails_whenDescriptionTooLong() {
        Film f = validFilm();
        f.setDescription("A".repeat(201));
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }


    @Test
    void createFilm_ok_whenReleaseDateEqualsBoundary() {
        Film f = validFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 28));
        Film saved = controller.createFilm(f);
    }

    @Test
    void createFilm_fails_whenReleaseDateBeforeBoundary() {
        Film f = validFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }

    @Test
    void createFilm_fails_whenReleaseDateNull() {
        Film f = validFilm();
        f.setReleaseDate(null);
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }

    @Test
    void createFilm_fails_whenDurationNull() {
        Film f = validFilm();
        f.setDuration(null);
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }

    @Test
    void createFilm_fails_whenDurationZero() {
        Film f = validFilm();
        f.setDuration(Duration.ZERO);
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }

    @Test
    void createFilm_fails_whenDurationNegative() {
        Film f = validFilm();
        f.setDuration(Duration.ofMinutes(-1));
        assertThrows(ValidationException.class, () -> controller.createFilm(f));
    }

    @Test
    void createFilm_fails_onEmptyRequest() {
        Film empty = new Film();
        assertThrows(ValidationException.class, () -> controller.createFilm(empty));
    }

    @Test
    void updateFilm_fails_whenIdMissing() {
        Film f = validFilm();
        f.setId(0);
        assertThrows(ValidationException.class, () -> controller.updateFilm(f));
    }

    @Test
    void updateFilm_fails_whenFilmNotFound() {
        Film f = validFilm();
        f.setId(999L);
        assertThrows(ValidationException.class, () -> controller.updateFilm(f));
    }

    @Test
    void updateFilm_updatesOnlyProvidedFields_andValidates() {
        Film saved = controller.createFilm(validFilm());

        Film patch = new Film();
        patch.setId(saved.getId());
        patch.setDescription("Новое описание");
        patch.setDuration(Duration.ZERO);

        assertThrows(ValidationException.class, () -> controller.updateFilm(patch));
    }

    @Test
    void getAll_empty() {
        Collection<Film> result = controller.getAll();

        assertTrue(result.isEmpty(), "Ожидаем пустой список пользователей");
    }

    @Test
    void getAll_notEmpty() {
        Film u = validFilm();
        Film u2 = validFilm();

        controller.createFilm(u);
        u2.setName("another@mail.ru");
        controller.createFilm(u2);

        Collection<Film> result = controller.getAll();
        assertEquals(2, result.size(), "Должны получить 2 пользователя");
    }
}