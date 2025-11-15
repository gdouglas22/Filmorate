package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FilmLikeStorage {
    void add(long filmId, long userId);

    void remove(long filmId, long userId);

    Set<Long> userIds(long filmId);
}