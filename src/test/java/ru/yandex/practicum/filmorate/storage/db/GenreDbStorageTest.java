package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import({GenreDbStorage.class, GenreRowMapper.class})
class GenreDbStorageTest extends AbstractDbStorageTest {

    @Autowired
    GenreDbStorage storage;

    @Test
    @DisplayName("findAllWithIds возвращает справочник жанров из БД")
    void findAllWithIds_returnsSeed() {
        List<GenreDto> dtos = storage.findAllWithIds();

        assertEquals(6, dtos.size());
        assertEquals("Комедия", dtos.get(0).getName());
        assertEquals("Боевик", dtos.get(5).getName());
    }

    @Test
    @DisplayName("findIdsByFilmId возвращает жанры фильма в порядке id")
    void findIdsByFilmId_returnsOrderedIds() {
        Set<Short> ids = storage.findIdsByFilmId(1);

        LinkedHashSet<Short> expected = new LinkedHashSet<>(List.of((short) 4, (short) 6));
        assertEquals(expected, ids);
    }

    @Test
    @DisplayName("findAllByIds возвращает жанры в порядке, заданном id")
    void findAllByIds_returnsOrderedGenres() {
        LinkedHashSet<Short> ids = new LinkedHashSet<>(List.of((short) 5, (short) 2));

        LinkedHashSet<Genre> expected = new LinkedHashSet<>(List.of(Genre.DOCUMENTARY, Genre.DRAMA));
        assertEquals(expected, storage.findAllByIds(ids));
    }

    @Test
    @DisplayName("findAllByIds выбрасывает исключение, если жанр не найден")
    void findAllByIds_missingGenre_throws() {
        LinkedHashSet<Short> ids = new LinkedHashSet<>(List.of((short) 1, (short) 99));

        assertThrows(NotFoundException.class, () -> storage.findAllByIds(ids));
    }
}
