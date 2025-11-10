package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validators.FilmValidator;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage films;
    @Qualifier("filmLikeDbStorage")
    private final FilmLikeStorage likes;
    private final UserService users;

    public List<FilmDto> findAll() {
        return films.findAll().stream().map(FilmMapper::mapToFilmDto).collect(Collectors.toList());
    }

    public FilmDto findById(long id) {
        return films.findById(id).map(FilmMapper::mapToFilmDto).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public FilmDto create(NewFilmRequest req) {
        Film f = FilmMapper.mapToFilm(req);
        validate(f);
        f = films.create(f);
        return FilmMapper.mapToFilmDto(f);
    }

    public FilmDto update(long id, UpdateFilmRequest req) {
        Film current = films.findById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Film updated = FilmMapper.applyUpdates(current, req);
        validate(updated);
        updated = films.update(updated);
        return FilmMapper.mapToFilmDto(updated);
    }

    public void addLike(long filmId, long userId) {
        films.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        users.findById(userId);
        likes.add(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        films.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        users.findById(userId);
        likes.remove(filmId, userId);
    }

    public List<FilmDto> getTop(int count) {
        return films.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId))
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (!FilmValidator.validate(film)) {
            throw new ValidationException("Ошибка валидации фильма");
        }
    }
}