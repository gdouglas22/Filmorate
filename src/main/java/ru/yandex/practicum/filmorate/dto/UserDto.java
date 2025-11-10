package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.annotations.NoSpaces;
import ru.yandex.practicum.filmorate.model.annotations.NotBlank;

import java.time.LocalDate;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String email;
    @NotBlank
    @NoSpaces
    private String login;
    private String name;
    private LocalDate birthday;
}
