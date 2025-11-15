package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(FriendshipDbStorage.class)
class FriendshipDbStorageTest extends AbstractDbStorageTest {

    @Autowired
    FriendshipDbStorage storage;

    @Test
    void getFriendsOf_ok() {
        var friends = storage.findForUser(1);
        assertEquals(1, friends.size());
    }

    @Test
    void addFriend_ok() {
        storage.upsert(1, 3, FriendshipStatus.CONFIRMED);

        var friends = storage.findForUser(1);
        assertEquals(2, friends.size());
        assertTrue(friends.containsValue(FriendshipStatus.CONFIRMED));
        //friends.keySet().stream().forEach(key -> System.out.println(key.equals(((long)3))));
        assertEquals(friends.keySet().contains((long) 3), true);
    }

    @Test
    void removeFriend_ok() {
        storage.delete(1, 2);

        var friends = storage.findForUser(1);
        assertTrue(friends.isEmpty());
    }

}
