package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage extends BaseRepository<MpaRating> implements MpaStorage {

    private static final String SQL_ALL = "select code from mpa_rating order by id";
    private static final String SQL_BY_ID = "select code from mpa_rating where id=?";
    private static final String SQL_ID_OF = "select id from mpa_rating where code=?";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<MpaRating> findAll() {
        return findMany(SQL_ALL);
    }

    @Override
    public Optional<MpaRating> findById(short id) {
        return findOne(SQL_BY_ID, id);
    }

    @Override
    public Optional<Short> idOf(MpaRating rating) {
        return jdbc.query(SQL_ID_OF, rs -> rs.next() ? Optional.of(rs.getShort(1)) : Optional.empty(), rating.name());
    }
}