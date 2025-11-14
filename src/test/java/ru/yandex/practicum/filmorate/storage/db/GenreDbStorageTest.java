package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@Sql(scripts = {"/schema.sql", "/sql/test-genres.sql"})
class GenreDbStorageTest {

    @Autowired GenreDbStorage genres;

    @Test
    void findAll_mapsEnumAndOrder() {
        var all = genres.findAll();
        assertThat(all).containsExactly(
                Genre.COMEDY, Genre.DRAMA, Genre.CARTOON, Genre.THRILLER, Genre.DOCUMENTARY, Genre.ACTION
        );
    }

    @Test
    void findAllWithIds_returnsDisplayNames() {
        var list = genres.findAllWithIds();
        GenreDto drama = list.stream().filter(g -> g.getId()==2).findFirst().orElseThrow();
        assertThat(drama.getName()).isEqualTo("Драма");
    }

    @Test
    void nameById_returnsLocalized() {
        assertThat(genres.nameById((short)2)).hasValue("Драма");
    }
}
