package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validators.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрошен список всех пользователей, всего: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Попытка создать пользователя: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        boolean isValid = UserValidator.validate(user);
        if (!isValid) {
            log.warn("Ошибка валидации при создании пользователя");
            throw new ValidationException("Ошибка валидации пользователя");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Попытка обновления пользователя: {}", newUser);

        if (newUser.getId() == 0) {
            log.warn("Попытка обновления без id");
            throw new ValidationException("Id должен быть указан");
        }

        User old = users.get(newUser.getId());
        if (old == null) {
            log.warn("Пользователь с id {} не найден для обновления", newUser.getId());
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null) old.setEmail(newUser.getEmail());
        if (newUser.getLogin() != null) old.setLogin(newUser.getLogin());
        if (newUser.getName() != null) old.setName(newUser.getName());
        if (newUser.getBirthday() != null) old.setBirthday(newUser.getBirthday());

        if (old.getName() == null || old.getName().isBlank()) {
            old.setName(old.getLogin());
        }

        boolean isValid = UserValidator.validate(old);
        if (!isValid) {
            log.warn("Ошибка валидации при обновлении id {}", old.getId());
            throw new ValidationException("Ошибка валидации пользователя");
        }

        users.put(old.getId(), old);
        log.info("Пользователь обновлён: {}", old);
        return old;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
