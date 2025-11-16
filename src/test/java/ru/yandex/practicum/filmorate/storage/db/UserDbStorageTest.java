package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import({UserDbStorage.class, UserRowMapper.class, FriendshipDbStorage.class})
class UserDbStorageTest extends AbstractDbStorageTest {

    @Autowired
    UserDbStorage storage;

    @Test
    @DisplayName("findById заполняет друзей пользователя")
    void findById_populatesFriends() {
        User user = storage.findById(1).orElseThrow();

        assertEquals("alice@example.com", user.getEmail());
        assertEquals(1, user.getFriends().size());
        assertEquals(FriendshipStatus.CONFIRMED, user.getFriends().get(2L));
    }

    @Test
    @DisplayName("create добавляет пользователя в базу")
    void create_persistsUser() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setLogin("neo");
        user.setName("Neo");
        user.setBirthday(LocalDate.of(1999, 12, 31));

        User saved = storage.create(user);

        assertTrue(saved.getId() > 0);
        assertEquals("new@example.com", saved.getEmail());

        User reloaded = storage.findById(saved.getId()).orElseThrow();
        assertEquals("neo", reloaded.getLogin());
    }

    @Test
    @DisplayName("update изменяет поля пользователя")
    void update_updatesUser() {
        User user = storage.findById(2).orElseThrow();
        user.setName("Bob Updated");
        user.setEmail("bob@update.com");

        User updated = storage.update(user);

        assertEquals("Bob Updated", updated.getName());
        assertEquals("bob@update.com", updated.getEmail());
    }
}
