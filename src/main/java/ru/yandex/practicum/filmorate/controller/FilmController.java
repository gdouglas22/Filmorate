package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validators.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрошен список всех фильмов, всего: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Попытка создать фильм: {}", film);
        boolean isValid = FilmValidator.validate(film);
        if (!isValid) {
            log.warn("Ошибка валидации при создании нового фильма");
            throw new ValidationException("Ошибка валидации фильма");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Попытка обновления фильма: {}", newFilm);
        if (newFilm.getId() == 0) {
            log.warn("Попытка обновления без id");
            throw new ValidationException("Id должен быть указан");
        }
        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Фильм с id {} не найден для обновления", newFilm.getId());
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }

        boolean isValid = FilmValidator.validate(oldFilm);
        if (!isValid) {
            log.warn("Ошибка валидации при обновлении id {}", oldFilm.getId());
            throw new ValidationException("Ошибка валидации фильма");
        }

        films.put(oldFilm.getId(), oldFilm);
        log.info("Фильм обновлён: {}", oldFilm);
        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}

