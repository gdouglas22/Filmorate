package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({MpaDbStorage.class, MpaRowMapper.class})
class MpaDbStorageTest extends AbstractDbStorageTest {

    @Autowired
    MpaDbStorage storage;

    @Test
    @DisplayName("findAll возвращает все рейтинги")
    void findAll_returnsRatings() {
        List<MpaRating> ratings = storage.findAll();
        assertEquals(5, ratings.size());
        assertEquals(MpaRating.G, ratings.get(0));
        assertEquals(MpaRating.NC_17, ratings.get(4));
    }

    @Test
    @DisplayName("findById возвращает рейтинг по id")
    void findById() {
        assertEquals(MpaRating.PG_13, storage.findById((short) 3).orElseThrow());
    }

    @Test
    @DisplayName("idOf возвращает id рейтинга")
    void idOf() {
        assertEquals(Short.valueOf((short) 4), storage.idOf(MpaRating.R).orElseThrow());
    }

    @Test
    @DisplayName("findAllWithIds возвращает DTO со значениями из enum")
    void findAllWithIds_returnsDto() {
        List<MpaDto> dtos = storage.findAllWithIds();

        assertEquals(5, dtos.size());
        assertEquals("PG-13", dtos.get(2).getName());
    }

    @Test
    @DisplayName("nameById возвращает display-name")
    void nameById() {
        assertEquals("PG-13", storage.nameById((short) 3).orElseThrow());
    }
}
