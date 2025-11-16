package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDtoVerbose;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmStorage filmStorage;

    public List<GenreDto> getAll() {
        return genreStorage.findAllWithIds();
    }

    public GenreDto getById(short id) {
        String name = genreStorage.nameById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден id=" + id));
        return new GenreDto(id, name);
    }

    public List<FilmDtoVerbose> getFilmsByGenre(short id) {
        getById(id);

        Map<Short, String> genreNames = genreStorage.findAllWithIds()
                .stream()
                .collect(Collectors.toMap(GenreDto::getId, GenreDto::getName));

        return filmStorage.findAll().stream()
                .filter(f -> genreStorage.findIdsByFilmId(f.getId()).contains(id))
                .collect(Collectors.toMap(Film::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                .values().stream()
                .map(f -> toVerboseDto(f, genreNames))
                .collect(Collectors.toList());
    }

    private FilmDtoVerbose toVerboseDto(Film film, Map<Short, String> genreNames) {
        short mpaId = mpaStorage.idOf(film.getMpaRating())
                .orElseThrow(() -> new NotFoundException("MPA не найден для фильма id=" + film.getId()));

        String mpaName = mpaStorage.nameById(mpaId)
                .or(() -> mpaStorage.codeById(mpaId))
                .orElse(film.getMpaRating().name());

        Set<Short> gids = genreStorage.findIdsByFilmId(film.getId());
        LinkedHashSet<GenreDto> genreDtos = gids.stream()
                .sorted(Short::compare)
                .map(gid -> new GenreDto(gid, genreNames.getOrDefault(gid, "")))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        FilmDtoVerbose dto = new FilmDtoVerbose();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(new MpaDto(mpaId, mpaName));
        dto.setGenres(genreDtos);
        dto.setLikes(film.getLikes().size());
        return dto;
    }
}
