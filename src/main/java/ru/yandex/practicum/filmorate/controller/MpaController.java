// src/main/java/ru/yandex/practicum/filmorate/controller/MpaController.java
package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaStorage mpaStorage;
    //private final MpaService mpaService;

    @GetMapping
    public List<MpaDto> getAll() {
        return mpaStorage.findAllWithIds();
    }

    @GetMapping({"/{id}", "/:{id}"})
    public MpaDto getOne(@PathVariable short id) {
        if (id <= 0) throw new ValidationException("Некорректный id");
        String displayName = mpaStorage.nameById(id)
                .orElseThrow(() -> new NotFoundException("MPA не найден id=" + id));
        return new MpaDto(id, displayName);
    }
}
