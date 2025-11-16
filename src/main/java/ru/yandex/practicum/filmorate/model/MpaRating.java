package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum MpaRating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String displayName;

    MpaRating(String displayName) {
        this.displayName = displayName;
    }
}