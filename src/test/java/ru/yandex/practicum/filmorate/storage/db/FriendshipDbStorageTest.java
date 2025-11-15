package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
