// GenreController.java
package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDtoVerbose;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmStorage filmStorage;

    @GetMapping
    public List<GenreDto> all() {
        return genreStorage.findAllWithIds(); // {id,name}
    }

    @GetMapping("/{id}")
    public GenreDto byId(@PathVariable short id) {
        String name = genreStorage.nameById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден id=" + id));
        return new GenreDto(id, name);
    }

    @GetMapping("/{id}/films")
    public List<FilmDtoVerbose> filmsByGenre(@PathVariable short id) {
        String gname = genreStorage.nameById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден id=" + id));

        // собираем карту имен жанров и id MPA → name, чтобы не дергать по одному
        Map<Short,String> genreNames = genreStorage.findAllWithIds()
                .stream().collect(Collectors.toMap(GenreDto::getId, GenreDto::getName));

        return filmStorage.findAll().stream()
                .filter(f -> genreStorage.findIdsByFilmId(f.getId()).contains(id))
                .collect(Collectors.toMap(Film::getId, Function.identity(), (a,b)->a, LinkedHashMap::new))
                .values().stream()
                .map(f -> {
                    short mpaId = mpaStorage.idOf(f.getMpaRating())
                            .orElseThrow(() -> new NotFoundException("MPA не найден для фильма id=" + f.getId()));
                    String mpaName = mpaStorage.nameById(mpaId)
                            .orElse(mpaStorage.codeById(mpaId).orElse(f.getMpaRating().name()));
                    // жанры фильма → verbose
                    Set<Short> gids = genreStorage.findIdsByFilmId(f.getId());
                    java.util.LinkedHashSet<GenreDto> gdtos = gids.stream()
                            .sorted(Short::compare)
                            .map(gid -> new GenreDto(gid, genreNames.getOrDefault(gid, "")))
                            .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

                    FilmDtoVerbose dto = new FilmDtoVerbose();
                    dto.setId(f.getId());
                    dto.setName(f.getName());
                    dto.setDescription(f.getDescription());
                    dto.setReleaseDate(f.getReleaseDate());
                    dto.setDuration(f.getDuration());
                    dto.setMpa(new ru.yandex.practicum.filmorate.dto.MpaDto(mpaId, mpaName));
                    dto.setGenres(gdtos);
                    dto.setLikes(f.getLikes().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

