package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static FilmDtoIds toIdsDto(Film f, Short mpaId, Set<Short> genreIds) {
        FilmDtoIds dto = new FilmDtoIds();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setDescription(f.getDescription());
        dto.setReleaseDate(f.getReleaseDate());
        dto.setDuration(f.getDuration());
        dto.setMpa(new IdRef(mpaId));
        dto.setGenres(genreIds.stream()
                .sorted(Short::compare)
                .map(IdRef::new)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new)));
        dto.setLikes(f.getLikes().size());
        return dto;
    }

    public static FilmDtoVerbose toVerboseDto(
            ru.yandex.practicum.filmorate.model.Film f,
            ru.yandex.practicum.filmorate.dto.MpaDto mpa,
            java.util.LinkedHashSet<ru.yandex.practicum.filmorate.dto.GenreDto> genres
    ) {
        var dto = new ru.yandex.practicum.filmorate.dto.FilmDtoVerbose();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setDescription(f.getDescription());
        dto.setReleaseDate(f.getReleaseDate());
        dto.setDuration(f.getDuration());
        dto.setMpa(mpa);
        dto.setGenres(genres);
        dto.setLikes(f.getLikes().size());
        return dto;
    }
}

