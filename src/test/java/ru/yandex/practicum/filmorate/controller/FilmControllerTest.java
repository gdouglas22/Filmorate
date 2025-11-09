package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilmControllerTest {

    private FilmService filmService;
    private FilmController controller;

    @BeforeEach
    void setUp() {
        filmService = Mockito.mock(FilmService.class);
        controller = new FilmController(filmService);
    }

    private Film validFilm() {
        Film f = new Film();
        f.setId(1L);
        f.setName("Test Film");
        f.setDescription("Описание");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(90);
        return f;
    }

    @Test
    void getAll_empty_returnsEmptyList() {
        when(filmService.getAll()).thenReturn(List.of());

        var result = controller.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(filmService, times(1)).getAll();
    }

    @Test
    void getAll_notEmpty_returnsList() {
        when(filmService.getAll()).thenReturn(List.of(validFilm(), validFilm()));

        var result = controller.getAll();

        assertEquals(2, result.size());
        verify(filmService, times(1)).getAll();
    }

    @Test
    void getById_ok_returnsFilm() {
        when(filmService.getById(42L)).thenReturn(validFilm());

        var film = controller.getById(42L);

        assertNotNull(film);
        assertEquals("Test Film", film.getName());
        verify(filmService, times(1)).getById(42L);
    }

    @Test
    void createFilm_ok_delegatesToServiceAndReturnsSaved() {
        var toCreate = validFilm();
        toCreate.setId(0); // обычно id ещё нет
        var saved = validFilm();
        saved.setId(100L);

        when(filmService.create(toCreate)).thenReturn(saved);

        var result = controller.createFilm(toCreate);

        assertEquals(100L, result.getId());
        assertEquals("Test Film", result.getName());
        verify(filmService, times(1)).create(toCreate);
    }

    @Test
    void createFilm_validationError_bubblesFromService() {
        var bad = new Film(); // заведомо некорректный
        when(filmService.create(bad)).thenThrow(new ValidationException("Ошибка валидации фильма"));

        assertThrows(ValidationException.class, () -> controller.createFilm(bad));
        verify(filmService, times(1)).create(bad);
    }

    @Test
    void updateFilm_ok_delegatesToServiceAndReturnsUpdated() {
        var patch = validFilm();
        patch.setId(10L);
        patch.setDescription("Новая");

        when(filmService.update(patch)).thenReturn(patch);

        var updated = controller.updateFilm(patch);

        assertEquals(10L, updated.getId());
        assertEquals("Новая", updated.getDescription());
        verify(filmService, times(1)).update(patch);
    }

    @Test
    void updateFilm_validationError_bubblesFromService() {
        var patch = validFilm();
        patch.setId(0L); // сервис должен кинуть
        when(filmService.update(patch)).thenThrow(new ValidationException("Id должен быть указан"));

        assertThrows(ValidationException.class, () -> controller.updateFilm(patch));
        verify(filmService, times(1)).update(patch);
    }

    @Test
    void addLike_callsService() {
        controller.addLike(5L, 7L);
        verify(filmService, times(1)).addLike(5L, 7L);
    }

    @Test
    void removeLike_callsService() {
        controller.removeLike(5L, 7L);
        verify(filmService, times(1)).removeLike(5L, 7L);
    }

    @Test
    void getPopular_delegatesToService() {
        when(filmService.getTop(3)).thenReturn(List.of(validFilm()));

        var list = controller.getPopular(3);

        assertEquals(1, list.size());
        verify(filmService, times(1)).getTop(3);
    }
}
