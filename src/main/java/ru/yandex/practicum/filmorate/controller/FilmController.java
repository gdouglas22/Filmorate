package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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

        nameValidation(film.getName());
        descriptionValidation(film.getDescription());
        issueDateValidation(film.getReleaseDate());
        durationValidation(film.getDuration());

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
            nameValidation(newFilm.getName());
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            descriptionValidation(newFilm.getDescription());
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            issueDateValidation(newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            durationValidation(newFilm.getDuration());
            oldFilm.setDuration(newFilm.getDuration());
        }

        log.info("Фильм обновлён: {}", oldFilm);
        return oldFilm;
    }

    private void nameValidation(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Ошибка валидации: имя пустое");
            throw new ValidationException("Имя обязательно");
        }
    }

    private void descriptionValidation(String desc) {
        if (desc == null || desc.isBlank()) {
            log.warn("Ошибка валидации: описание пустое");
            throw new ValidationException("Описание обязательно");
        }
        if (desc.length() > 200) {
            log.warn("Ошибка валидации: описание слишком длинное ({} символов)", desc.length());
            throw new ValidationException("Описание не может быть больше 200 символов");
        }
    }

    private void issueDateValidation(LocalDate date) {
        if (date == null) {
            log.warn("Ошибка валидации: дата релиза не указана");
            throw new ValidationException("Дата релиза обязательна");
        }
        if (date.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: дата релиза {} раньше 28.12.1895", date);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void durationValidation(Integer duration) {
        if (duration == null) {
            log.warn("Ошибка валидации: длительность не указана");
            throw new ValidationException("Продолжительность обязательна");
        }
        if (duration <= 0) {
            log.warn("Ошибка валидации: длительность некорректна ({})", duration);
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

