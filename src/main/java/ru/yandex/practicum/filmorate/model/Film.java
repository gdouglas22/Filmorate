package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.annotations.*;

import java.time.LocalDate;

@Data
public class Film {
    private long id;

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Описание обязательно")
    @MaxLen(value = 200, message = "Описание не может быть больше 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @NotBefore(value = "1895-12-28", message = "Дата релиза не может быть раньше 28.12.1895")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность обязательна")
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
}