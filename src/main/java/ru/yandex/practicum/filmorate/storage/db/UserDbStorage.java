package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

@Profile("db")
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private final FriendshipStorage friendshipStorage;

    private static final String SQL_FIND_ALL = """
            SELECT id, email, login, name, birthday
            FROM users
            ORDER BY id
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT id, email, login, name, birthday
            FROM users
            WHERE id = ?
            """;

    private static final String SQL_INSERT = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper,
                         @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        super(jdbc, mapper);
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public Collection<User> findAll() {
        var list = findMany(SQL_FIND_ALL);
        list.forEach(this::fillFriends);
        return list;
    }

    @Override
    public Optional<User> findById(long id) {
        var opt = findOne(SQL_FIND_BY_ID, id);
        opt.ifPresent(this::fillFriends);
        return opt;
    }

    @Override
    public User create(User u) {
        long id = insert(
                SQL_INSERT,
                u.getEmail(),
                u.getLogin(),
                u.getName(),
                Date.valueOf(u.getBirthday())
        );
        u.setId(id);
        return findById(id).orElse(u);
    }

    @Override
    public User update(User u) {
        int n = update(
                SQL_UPDATE,
                u.getEmail(),
                u.getLogin(),
                u.getName(),
                Date.valueOf(u.getBirthday()),
                u.getId()
        );
        if (n == 0) throw new IllegalStateException("user not updated id=" + u.getId());
        return findById(u.getId()).orElse(u);
    }

    private void fillFriends(User u) {
        u.getFriends().clear();
        u.getFriends().putAll(friendshipStorage.findForUser(u.getId()));
    }
}
