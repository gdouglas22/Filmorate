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
    public UserDto getById(@PathVariable long id) {
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
    public UserDto update(@PathVariable long id, @RequestBody UpdateUserRequest req) {
        log.info("Попытка обновления пользователя id={}", id);
        var updated = userService.update(id, req);
        log.info("Пользователь обновлён id={}", updated.getId());
        return updated;
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Добавление в друзья: {} -> {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Удаление из друзей: {} -> {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> listFriends(@PathVariable long id) {
        var list = userService.getFriends(id);
        log.info("Список друзей пользователя {}: {}", id, list.size());
        return list;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> commonFriends(@PathVariable long id, @PathVariable long otherId) {
        var list = userService.getCommonFriends(id, otherId);
        log.info("Общие друзья у {} и {}: {}", id, otherId, list.size());
        return list;
    }
}
