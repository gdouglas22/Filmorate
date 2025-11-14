package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.validators.FilmValidator;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage films;
    @Qualifier("filmLikeDbStorage")
    private final FilmLikeStorage likes;

    @Qualifier("mpaDbStorage")
    private final MpaStorage mpaStorage;
    @Qualifier("genreDbStorage")
    private final GenreStorage genreStorage;

    private final UserService users;

    private static short requireId(IdRef ref, String what) {
        if (ref == null || ref.getId() == null)
            throw new ValidationException(what + " обязателен");
        return ref.getId();
    }

    public List<FilmDtoIds> findAll() {
        return films.findAll().stream().map(f -> {
            short mpaId = mpaStorage.idOf(f.getMpaRating())
                    .orElseThrow(() -> new NotFoundException("MPA не найден для фильма id=" + f.getId()));
            Set<Short> genreIds = genreStorage.findIdsByFilmId(f.getId());
            return FilmMapper.toIdsDto(f, mpaId, genreIds);
        }).collect(Collectors.toList());
    }

//    public FilmDtoIds findById(long id) {
//        Film f = films.findById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
//        short mpaId = mpaStorage.idOf(f.getMpaRating())
//                .orElseThrow(() -> new NotFoundException("MPA не найден для фильма id=" + id));
//        Set<Short> genreIds = genreStorage.findIdsByFilmId(f.getId());
//        return FilmMapper.toIdsDto(f, mpaId, genreIds);
//    }


    public FilmDtoVerbose findById(long id) {
        Film f = films.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        short mpaId = mpaStorage.idOf(f.getMpaRating())
                .orElseThrow(() -> new NotFoundException("MPA не найден для фильма id=" + id));

        // Человеческое имя рейтинга
        String mpaName = mpaStorage.codeById(mpaId)
                .map(code -> {
                    // если есть enum с displayName
                    try { return MpaRating.valueOf(code).getDisplayName(); }
                    catch (IllegalArgumentException e) { return code.replace('_','-'); }
                })
                .orElse(f.getMpaRating().name());

        // Жанры: id + локализованное имя (на русском)
        java.util.LinkedHashSet<GenreDto> genres = genreStorage.findIdsByFilmId(f.getId()).stream()
                .sorted(Short::compare)
                .map(gid -> {
                    String name = genreStorage.nameById(gid)
                            .orElseGet(() -> Genre.valueOf(
                                    genreStorage.codeById(gid).orElseThrow()
                            ).getDisplayName());
                    return new GenreDto(gid, name);
                })
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));

        MpaDto mpa = new MpaDto(mpaId, mpaName);
        return FilmMapper.toVerboseDto(f, mpa, genres);
    }

    public FilmDtoIds create(NewFilmRequest req) {
        Film f = new Film();
        f.setName(req.getName());
        f.setDescription(req.getDescription());
        f.setReleaseDate(req.getReleaseDate());
        f.setDuration(req.getDuration());

        short mpaId = requireId(req.getMpa(), "MPA");
        String mpaCode = mpaStorage.codeById(mpaId)
                .orElseThrow(() -> new NotFoundException("MPA не найден id=" + mpaId));
        f.setMpaRating(MpaRating.valueOf(mpaCode));

        Set<Short> genreIds = (req.getGenres() == null ? Set.<IdRef>of() : req.getGenres())
                .stream().map(IdRef::getId)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        for (Short gid : genreIds) {
            genreStorage.codeById(gid)
                    .orElseThrow(() -> new NotFoundException("Жанр не найден id=" + gid));
        }

        validate(f);
        f = films.create(f);
        genreStorage.replaceForFilmByIds(f.getId(), genreIds);

        return FilmMapper.toIdsDto(f, mpaId, genreIds);
    }

    public FilmDtoIds update(long id, UpdateFilmRequest req) {
        Film current = films.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        // применяем частичные обновления базовых полей
        if (req.getName() != null && !req.getName().isBlank()) current.setName(req.getName());
        if (req.getDescription() != null && !req.getDescription().isBlank()) current.setDescription(req.getDescription());
        if (req.getReleaseDate() != null) current.setReleaseDate(req.getReleaseDate());
        if (req.getDuration() != null) current.setDuration(req.getDuration());

        Short mpaIdOut;
        if (req.getMpa() != null && req.getMpa().getId() != null) {
            short mpaId = req.getMpa().getId();
            String mpaCode = mpaStorage.codeById(mpaId)
                    .orElseThrow(() -> new NotFoundException("MPA не найден id=" + mpaId));
            current.setMpaRating(MpaRating.valueOf(mpaCode));
            mpaIdOut = mpaId;
        } else {
            mpaIdOut = mpaStorage.idOf(current.getMpaRating())
                    .orElseThrow(() -> new NotFoundException("MPA отсутствует у фильма id=" + id));
        }

        Set<Short> genreIdsOut;
        if (req.getGenres() != null) {
            Set<Short> ids = req.getGenres().stream().map(IdRef::getId)
                    .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
            for (Short gid : ids) {
                genreStorage.codeById(gid)
                        .orElseThrow(() -> new NotFoundException("Жанр не найден id=" + gid));
            }
            genreStorage.replaceForFilmByIds(id, ids);
            genreIdsOut = ids;
        } else {
            genreIdsOut = genreStorage.findIdsByFilmId(id);
        }

        validate(current);
        current = films.update(current);

        return FilmMapper.toIdsDto(current, mpaIdOut, genreIdsOut);
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

    public List<FilmDtoIds> getTop(int count) {
        return films.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size())
                        .reversed()
                        .thenComparing(Film::getId))
                .limit(count)
                .map(f -> {
                    short mpaId = mpaStorage.idOf(f.getMpaRating())
                            .orElseThrow(() -> new NotFoundException("MPA отсутствует у фильма id=" + f.getId()));
                    Set<Short> genreIds = genreStorage.findIdsByFilmId(f.getId());
                    return FilmMapper.toIdsDto(f, mpaId, genreIds);
                })
                .toList();
    }

    private void validate(Film film) {
        if (!FilmValidator.validate(film)) {
            throw new ValidationException("Ошибка валидации фильма");
        }
    }
}