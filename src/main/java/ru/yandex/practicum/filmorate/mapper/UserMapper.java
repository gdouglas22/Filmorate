package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(NewUserRequest r) {
        return new User(0, r.getEmail(), r.getLogin(), r.getName(), r.getBirthday());
    }

    public static UserDto mapToUserDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setLogin(u.getLogin());
        dto.setName(u.getName());
        dto.setBirthday(u.getBirthday());
        return dto;
    }

    public static User applyUpdates(User u, UpdateUserRequest r) {
        if (r.hasEmail()) u.setEmail(r.getEmail());
        if (r.hasLogin()) u.setLogin(r.getLogin());
        if (r.hasName()) u.setName(r.getName());
        if (r.hasBirthday()) u.setBirthday(r.getBirthday());
        return u;
    }
}
