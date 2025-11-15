package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDtoVerbose;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> all() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public GenreDto byId(@PathVariable short id) {
        return genreService.getById(id);
    }

    @GetMapping("/{id}/films")
    public List<FilmDtoVerbose> filmsByGenre(@PathVariable short id) {
        return genreService.getFilmsByGenre(id);
    }
}
