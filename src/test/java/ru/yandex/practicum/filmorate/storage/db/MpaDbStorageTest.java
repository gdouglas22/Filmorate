// src/test/java/ru/yandex/practicum/filmorate/storage/db/MpaDbStorageTest.java
package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@Sql(scripts = {"/schema.sql", "/sql/test-mpa.sql"})
class MpaDbStorageTest {

    @Autowired MpaDbStorage mpa;

    @Test
    void findAll_returnsEnums() {
        var all = mpa.findAll();
        assertThat(all).containsExactly(MpaRating.G, MpaRating.PG, MpaRating.PG_13, MpaRating.R, MpaRating.NC_17);
    }

    @Test
    void findAllWithIds_returnsDisplayNames() {
        var list = mpa.findAllWithIds();
        assertThat(list.get(2).getName()).isEqualTo("PG-13");
    }

    @Test
    void nameById_isDisplayName() {
        assertThat(mpa.nameById((short)5)).hasValue("NC-17");
    }
}
