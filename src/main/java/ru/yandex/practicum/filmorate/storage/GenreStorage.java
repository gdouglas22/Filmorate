package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(short id);

    Set<Short> findIdsByFilmId(long filmId);

    Optional<Short> idOf(Genre genre);

    Set<Genre> findByFilmId(long filmId);

    void replaceForFilm(long filmId, Set<Genre> genres);

    Optional<String> nameById(short id);

    Set<Genre> findAllByIds(Set<Short> genreIds);

    List<GenreDto> findAllWithIds();

    default Optional<String> codeById(short id) {
        return findById(id).map(Enum::name);
    }
}
