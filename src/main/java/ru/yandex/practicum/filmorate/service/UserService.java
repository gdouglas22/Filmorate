package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validators.UserValidator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage storage;

    public Collection<User> getAll() {
        return storage.findAll();
    }

    public User create(User user) {
        ensureNameFallback(user);
        validate(user);
        return storage.create(user);
    }

    public User update(User patch) {
        if (patch.getId() == 0) throw new ValidationException("Id должен быть указан");

        User current = storage.findById(patch.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + patch.getId() + " не найден"));

        if (patch.getEmail() != null) current.setEmail(patch.getEmail());
        if (patch.getLogin() != null) current.setLogin(patch.getLogin());
        if (patch.getName() != null) current.setName(patch.getName());
        if (patch.getBirthday() != null) current.setBirthday(patch.getBirthday());

        ensureNameFallback(current);
        validate(current);
        return storage.update(current);
    }

    public User getById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    // Сейчас сразу подтверждаем дружбу (как раньше при Set<Long>)
    public void addFriend(long userId, long friendId) {
        if (userId == friendId) throw new ValidationException("Нельзя добавить в друзья самого себя");
        User u = getById(userId);
        User f = getById(friendId);

        u.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
        f.getFriends().put(userId, FriendshipStatus.CONFIRMED);

        storage.update(u);
        storage.update(f);
        log.info("Пользователи {} и {} теперь друзья (CONFIRMED)", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User u = getById(userId);
        User f = getById(friendId);

        u.getFriends().remove(friendId);
        f.getFriends().remove(userId);

        storage.update(u);
        storage.update(f);
        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(long userId) {
        User u = getById(userId);
        return u.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> getById(e.getKey()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User u1 = getById(userId);
        User u2 = getById(otherId);

        Set<Long> f1 = u1.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Long> f2 = u2.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        f1.retainAll(f2); // пересечение

        return f1.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    private void ensureNameFallback(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validate(User user) {
        if (!UserValidator.validate(user)) {
            throw new ValidationException("Ошибка валидации пользователя");
        }
    }
}