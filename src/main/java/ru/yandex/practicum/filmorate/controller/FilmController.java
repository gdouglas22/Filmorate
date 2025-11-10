package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> getAll() {
        var all = filmService.findAll();
        log.info("Запрошен список всех фильмов, всего: {}", all.size());
        return all;
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable long id) {
        log.info("Запрошен фильм id={}", id);
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@RequestBody NewFilmRequest req) {
        log.info("Создание фильма");
        var created = filmService.create(req);
        log.info("Фильм создан id={}", created.getId());
        return created;
    }

    @PutMapping("/{id}")
    public FilmDto update(@PathVariable long id, @RequestBody UpdateFilmRequest req) {
        log.info("Обновление фильма id={}", id);
        var updated = filmService.update(id, req);
        log.info("Фильм обновлён id={}", updated.getId());
        return updated;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count) {
        var list = filmService.getTop(count);
        log.info("Топ {} фильмов, найдено {}", count, list.size());
        return list;
    }
}
