package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(NewFilmRequest r) {
        Film f = new Film();
        f.setName(r.getName());
        f.setDescription(r.getDescription());
        f.setReleaseDate(r.getReleaseDate());
        f.setDuration(r.getDuration());
        f.setMpaRating(r.getMpaRating());
        if (r.getGenres() != null) f.getGenres().addAll(r.getGenres());
        return f;
    }

    public static FilmDto mapToFilmDto(Film f) {
        FilmDto dto = new FilmDto();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setDescription(f.getDescription());
        dto.setReleaseDate(f.getReleaseDate());
        dto.setDuration(f.getDuration());
        dto.setMpaRating(f.getMpaRating());
        dto.setGenres(f.getGenres());
        dto.setLikes(f.getLikes().size());
        return dto;
    }

    public static Film applyUpdates(Film f, UpdateFilmRequest r) {
        if (r.hasName()) f.setName(r.getName());
        if (r.hasDescription()) f.setDescription(r.getDescription());
        if (r.hasReleaseDate()) f.setReleaseDate(r.getReleaseDate());
        if (r.hasDuration()) f.setDuration(r.getDuration());
        if (r.hasMpa()) f.setMpaRating(r.getMpaRating());
        if (r.hasGenres()) {
            f.getGenres().clear();
            f.getGenres().addAll(r.getGenres());
        }
        return f;
    }
}
