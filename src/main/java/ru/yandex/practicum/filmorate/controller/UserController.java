package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        var all = userService.getAll();
        log.info("Запрошен список всех пользователей, всего: {}", all.size());
        return all;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        log.info("Запрошен пользователь id={}", id);
        return userService.getById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Попытка создать пользователя: {}", user);
        User created = userService.create(user);
        log.info("Пользователь создан: {}", created);
        return created;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Попытка обновления пользователя: {}", newUser);
        User updated = userService.update(newUser);
        log.info("Пользователь обновлён: {}", updated);
        return updated;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Добавление в друзья: {} -> {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Удаление из друзей: {} -> {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> listFriends(@PathVariable long id) {
        var list = userService.getFriends(id);
        log.info("Список друзей пользователя {}: {}", id, list.size());
        return list;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable long id, @PathVariable long otherId) {
        var list = userService.getCommonFriends(id, otherId);
        log.info("Общие друзья у {} и {}: {}", id, otherId, list.size());
        return list;
    }
}