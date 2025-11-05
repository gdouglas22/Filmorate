package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validators.FilmValidator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    public Collection<Film> getAll() {
        return storage.findAll();
    }

    public Film getById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Film create(Film film) {
        validate(film);
        return storage.create(film);
    }

    public Film update(Film patch) {
        if (patch.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }
        Film current = storage.findById(patch.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + patch.getId() + " не найден"));

        if (patch.getName() != null) current.setName(patch.getName());
        if (patch.getDescription() != null) current.setDescription(patch.getDescription());
        if (patch.getReleaseDate() != null) current.setReleaseDate(patch.getReleaseDate());
        if (patch.getDuration() != null) current.setDuration(patch.getDuration());

        validate(current);
        return storage.update(current);
    }

    public void addLike(long filmId, long userId) {
        Film film = storage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userService.getById(userId);
        film.getLikes().add(userId);
        storage.update(film);
    }

    public void removeLike(long filmId, long userId) {
        Film film = storage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userService.getById(userId);
        film.getLikes().remove(userId);
        storage.update(film);
    }

    public List<Film> getTop(int count) {
        return storage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed()
                        .thenComparing(Film::getId))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (!FilmValidator.validate(film)) {
            throw new ValidationException("Ошибка валидации фильма");
        }
    }
}

