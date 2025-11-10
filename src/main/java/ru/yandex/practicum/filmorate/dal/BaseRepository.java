// src/main/java/ru/yandex/practicum/filmorate/storage/db/BaseRepository.java
package ru.yandex.practicum.filmorate.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String sql, Object... params) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, mapper, params));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String sql, Object... params) {
        return jdbc.query(sql, mapper, params);
    }

    protected long insert(String sql, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
            Object idObj = keyHolder.getKeys().getOrDefault("id",
                    keyHolder.getKeys().getOrDefault("ID", null));
            if (idObj instanceof Number n) return n.longValue();
            for (Object v : keyHolder.getKeys().values()) {
                if (v instanceof Number n2) return n2.longValue();
            }
        }
        Number single = keyHolder.getKey();
        if (single != null) return single.longValue();

        throw new IllegalStateException("Не удалось получить сгенерированный id");
    }

    protected int update(String sql, Object... params) {
        return jdbc.update(sql, params);
    }
}
