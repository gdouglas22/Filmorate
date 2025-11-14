package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Genre;
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
    private static final String SQL_ALL_WITH_IDS = "select id, code from mpa_rating order by id";

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

    @Override
    public List<MpaDto> findAllWithIds() {
        return jdbc.query(SQL_ALL_WITH_IDS, (rs, rowNum) -> {
            short id = rs.getShort("id");
            String code = rs.getString("code");
            String displayName;
            try {
                displayName = MpaRating.valueOf(code).getDisplayName();
            } catch (IllegalArgumentException e) {
                displayName = code.replace('_','-');
            }
            return new MpaDto(id, displayName);
        });
    }

    @Override
    public Optional<String> nameById(short id) {
        return jdbc.query("SELECT code FROM mpa_rating WHERE id=?",
                rs -> {
                    if (!rs.next()) return Optional.empty();
                    String code = rs.getString("code");
                    return Optional.of(MpaRating.valueOf(code).getDisplayName());
                },
                id
        );
    }
}