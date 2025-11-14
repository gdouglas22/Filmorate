package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendshipDbStorage.class, UserDbStorage.class, ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper.class})
@Sql(scripts = {"/schema.sql", "/sql/test-users.sql"})
class FriendshipDbStorageTest {

    @Autowired FriendshipDbStorage friendships;

    @Test
    void upsert_and_findForUser_ok() {
        friendships.upsert(2L, 1L, FriendshipStatus.CONFIRMED);

        var mapUser2 = friendships.findForUser(2L);
        var mapUser1 = friendships.findForUser(1L);

        assertThat(mapUser2).hasSize(1);
        assertThat(mapUser2).containsKey(1L);

        assertThat(mapUser1).isEmpty(); // односторонняя
    }

    @Test
    void delete_ok() {
        friendships.upsert(2L, 1L, FriendshipStatus.CONFIRMED);
        friendships.delete(2L, 1L);
        assertThat(friendships.findForUser(2L)).isEmpty();
    }
}
