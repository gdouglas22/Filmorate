package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.annotations.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private long id;

    @NotBlank(message = "Email обязателен")
    @CorrectEmail(message = "Email должен содержать '@' и не начинаться/заканчиваться им")
    private String email;

    @NotBlank(message = "Логин обязателен")
    @NoSpaces(message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения обязательна")
    @NotFuture(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private final Map<Long, FriendshipStatus> friends = new HashMap<>();
}
