package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;


public interface MpaStorage {
    List<MpaRating> findAll();
    Optional<MpaRating> findById(short id);
    Optional<Short> idOf(MpaRating rating);
    default Optional<String> codeById(short id) {
        return findById(id).map(Enum::name);
    }
    Optional<String> nameById(short id);
    List<MpaDto> findAllWithIds();
}
