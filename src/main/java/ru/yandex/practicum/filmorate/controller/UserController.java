package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    final String emailRegex = ".*@.*";

    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрос списка всех пользователей, всего: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Попытка создания пользователя: {}", user);

        emailValidation(user.getEmail());
        loginValidation(user.getLogin());
        birthdayDateValidation(user.getBirthdayDate());

        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);

        log.info("Пользователь успешно создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Попытка обновления пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.warn("Не передан id при обновлении");
            throw new ValidationException("Id должен быть указан");
        }

        User old = users.get(newUser.getId());
        if (old == null) {
            log.warn("Пользователь с id {} не найден для обновления", newUser.getId());
            throw new ValidationException("User с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null) {
            String email = newUser.getEmail().trim();
            if (!email.equalsIgnoreCase(old.getEmail())) {
                emailValidation(email);
                old.setEmail(email);
            }
        }

        if (newUser.getLogin() != null) {
            loginValidation(newUser.getLogin());
            old.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            old.setName(newUser.getName());
        }
        if (newUser.getBirthdayDate() != null) {
            birthdayDateValidation(newUser.getBirthdayDate());
            old.setBirthdayDate(newUser.getBirthdayDate());
        }

        log.info("Пользователь обновлён: {}", old);
        return old;
    }

    private boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(u -> email.equals(u.getEmail()));
    }

    private void emailValidation(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Почта обязательна");
        }
        if (!email.matches(emailRegex)) {
            throw new ValidationException("Неверный формат почты: пропущен @");
        }
        if (existsByEmail(email)) {
            throw new ValidationException("Эта почта уже используется");
        }
    }

    private void loginValidation(String login) {
        if (login == null || login.isBlank()) {
            throw new ValidationException("Логин обязателен");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Неверный формат логина: в имени не должно быть пробелов");
        }
    }

    private void birthdayDateValidation(LocalDate birthdayDate) {
        if (birthdayDate == null) {
            throw new ValidationException("Дата рождения обязательна");
        }
        if (!birthdayDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть сегодня или в будущем");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
