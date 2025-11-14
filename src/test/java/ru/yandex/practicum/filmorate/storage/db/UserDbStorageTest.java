// src/test/java/ru/yandex/practicum/filmorate/storage/db/UserDbStorageTest.java
package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@Sql(scripts = {"/schema.sql", "/sql/test-users.sql"})
class UserDbStorageTest {

    @Autowired UserDbStorage storage;

    @Test
    void findAll_returnsSeeded() {
        assertThat(storage.findAll()).hasSize(2);
    }

    @Test
    void findById_ok() {
        var u = storage.findById(1).orElseThrow();
        assertThat(u.getEmail()).isEqualTo("u1@mail.com");
    }

    @Test
    void create_and_update_ok() {
        User u = new User();
        u.setEmail("new@mail.com");
        u.setLogin("new_login");
        u.setName("New");
        u.setBirthday(LocalDate.of(1990,1,1));

        var created = storage.create(u);
        assertThat(created.getId()).isPositive();

        created.setName("Updated");
        var updated = storage.update(created);
        assertThat(updated.getName()).isEqualTo("Updated");
    }
}
