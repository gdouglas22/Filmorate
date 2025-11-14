// src/main/java/ru/yandex/practicum/filmorate/controller/UserController.java
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        var all = userService.findAll();
        log.info("Запрошен список всех пользователей, всего: {}", all.size());
        return all;
    }

    @GetMapping("/{id}")
    public UserDto getById(@Valid @PathVariable long id) {
        log.info("Запрошен пользователь id={}", id);
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest req) {
        log.info("Попытка создать пользователя");
        var created = userService.create(req);
        log.info("Пользователь создан id={}", created.getId());
        return created;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateById(@PathVariable long id, @Valid @RequestBody UpdateUserRequest req) {
        log.info("[PUT /users/{{}}] Тело: {}", id, req);
        var updated = userService.update(id, req);
        log.info("Пользователь обновлён по id в URL: {}", id);
        return updated;
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Добавление в друзья: {} -> {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateWithBody(@Valid @RequestBody UpdateUserRequest req) {
        if (req.getId() == null || req.getId() <= 0) {
            throw new ValidationException("Поле id обязательно для обновления пользователя");
        }
        log.info("[PUT /users] Тело: {}", req);
        var updated = userService.update(req.getId(), req);
        log.info("Пользователь обновлён по id из тела: {}", req.getId());
        return updated;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable @Valid long id, @PathVariable long friendId) {
        log.info("Удаление из друзей: {} -> {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> listFriends(@PathVariable @Valid long id) {
        var list = userService.getFriends(id);
        log.info("Список друзей пользователя {}: {}", id, list.size());
        return list;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> commonFriends(@PathVariable @Valid long id, @PathVariable long otherId) {
        var list = userService.getCommonFriends(id, otherId);
        log.info("Общие друзья у {} и {}: {}", id, otherId, list.size());
        return list;
    }
}
