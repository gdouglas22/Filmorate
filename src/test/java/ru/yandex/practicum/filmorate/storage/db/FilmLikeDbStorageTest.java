package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(FilmLikeDbStorage.class)
class FilmLikeDbStorageTest extends AbstractDbStorageTest {

    @Autowired
    FilmLikeDbStorage storage;

    @Test
    @DisplayName("userIds возвращает лайки фильма")
    void userIds_returnsOrderedSet() {
        Set<Long> ids = storage.userIds(1);
        assertEquals(List.of(1L, 2L), ids.stream().toList());
    }

    @Test
    @DisplayName("add/remove изменяют лайки фильма")
    void addAndRemove_updateTable() {
        storage.add(2, 3);
        assertTrue(storage.userIds(2).contains(3L));

        storage.remove(2, 1);
        assertEquals(List.of(3L), storage.userIds(2).stream().toList());
    }
}
