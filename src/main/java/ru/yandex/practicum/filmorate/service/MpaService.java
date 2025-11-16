package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<MpaDto> getAll() {
        return mpaStorage.findAllWithIds();
    }

    public MpaDto getOne(short id) {
        if (id <= 0) {
            throw new ValidationException("Некорректный id");
        }
        String displayName = mpaStorage.nameById(id)
                .orElseThrow(() -> new NotFoundException("MPA не найден id=" + id));
        return new MpaDto(id, displayName);
    }
}
