package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private IdRef mpa;
    private Set<IdRef> genres;

    public boolean hasName() { return name != null && !name.isBlank(); }
    public boolean hasDescription() { return description != null && !description.isBlank(); }
    public boolean hasReleaseDate() { return releaseDate != null; }
    public boolean hasDuration() { return duration != null; }
    public boolean hasMpa() { return mpa != null; }
    public boolean hasGenres() { return genres != null; }
}
