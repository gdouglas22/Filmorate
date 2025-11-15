package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Map;
import java.util.Optional;

public interface FriendshipStorage {
    void upsert(long requesterId, long addresseeId, FriendshipStatus status);

    void delete(long requesterId, long addresseeId);

    Map<Long, FriendshipStatus> findForUser(long requesterId);

    Optional<FriendshipStatus> getStatus(long requesterId, long addresseeId);
}
