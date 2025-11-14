// src/main/java/ru/yandex/practicum/filmorate/service/UserService.java
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage users;
    @Qualifier("friendshipDbStorage")
    private final FriendshipStorage friendships;

    public UserDto create(NewUserRequest req) {
        User u = UserMapper.mapToUser(req);
        if (u.getName() == null || u.getName().isBlank()) u.setName(u.getLogin());
        u = users.create(u);
        return UserMapper.mapToUserDto(u);
    }

    public UserDto update(long userId, UpdateUserRequest req) {
        User u = users.findById(userId).orElseThrow(NoSuchElementException::new);
        u = UserMapper.applyUpdates(u, req);
        if (u.getName() == null || u.getName().isBlank()) u.setName(u.getLogin());
        u = users.update(u);
        return UserMapper.mapToUserDto(u);
    }

    public List<UserDto> findAll() {
        return users.findAll().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto findById(long userId) {
        return users.findById(userId).map(UserMapper::mapToUserDto).orElseThrow(NoSuchElementException::new);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) throw new IllegalArgumentException("Нельзя добавить себя");
        users.findById(userId).orElseThrow(NoSuchElementException::new);
        users.findById(friendId).orElseThrow(NoSuchElementException::new);
        friendships.upsert(userId, friendId, FriendshipStatus.CONFIRMED);
    }

    public void removeFriend(long userId, long friendId) {
        users.findById(userId).orElseThrow(NoSuchElementException::new);
        users.findById(friendId).orElseThrow(NoSuchElementException::new);
        friendships.delete(userId, friendId);
    }

    public List<UserDto> getFriends(long userId) {
        users.findById(userId).orElseThrow(NoSuchElementException::new);
        var map = friendships.findForUser(userId);
        return map.entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> users.findById(e.getKey()).orElseThrow(NoSuchElementException::new))
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(long userId, long otherId) {
        users.findById(userId).orElseThrow(NoSuchElementException::new);
        users.findById(otherId).orElseThrow(NoSuchElementException::new);
        Set<Long> a = friendships.findForUser(userId).entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> e.getKey()).collect(Collectors.toSet());
        Set<Long> b = friendships.findForUser(otherId).entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> e.getKey()).collect(Collectors.toSet());
        a.retainAll(b);
        return a.stream()
                .map(id -> users.findById(id).orElseThrow(NoSuchElementException::new))
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
