package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("friendshipDbStorage")
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbc;

    public FriendshipDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void upsert(long requesterId, long addresseeId, FriendshipStatus status) {
        jdbc.update("""
                merge into friendship(requester_id,addressee_id,status)
                key(requester_id,addressee_id) values(?,?,?)
                """, requesterId, addresseeId, status.name());
    }

    @Override
    public void delete(long requesterId, long addresseeId) {
        jdbc.update("delete from friendship where requester_id=? and addressee_id=?", requesterId, addresseeId);
    }

    @Override
    public Map<Long, FriendshipStatus> findForUser(long userId) {
        List<Map.Entry<Long, FriendshipStatus>> rows = jdbc.query(
                "select addressee_id, status from friendship where requester_id = ? order by addressee_id",
                (rs, n) -> Map.entry(rs.getLong(1), FriendshipStatus.valueOf(rs.getString(2))),
                userId
        );
        Map<Long, FriendshipStatus> map = new LinkedHashMap<>();
        for (var e : rows) map.put(e.getKey(), e.getValue());
        return map;
    }

    @Override
    public Optional<FriendshipStatus> getStatus(long requesterId, long addresseeId) {
        return jdbc.query("select status from friendship where requester_id=? and addressee_id=?", rs -> rs.next() ? Optional.of(FriendshipStatus.valueOf(rs.getString(1))) : Optional.empty(), requesterId, addresseeId);
    }
}
