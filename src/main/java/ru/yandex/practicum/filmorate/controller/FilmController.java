package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        var all = filmService.getAll();
        log.info("Запрошен список всех фильмов, всего: {}", all.size());
        return all;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        log.info("Запрошен фильм id={}", id);
        return filmService.getById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Попытка создать фильм: {}", film);
        Film created = filmService.create(film);
        log.info("Фильм создан: {}", created);
        return created;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Попытка обновления фильма: {}", newFilm);
        Film updated = filmService.update(newFilm);
        log.info("Фильм обновлён: {}", updated);
        return updated;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        var list = filmService.getTop(count);
        log.info("Запрошен топ {} популярных фильмов, найдено {}", count, list.size());
        return list;
    }
}
