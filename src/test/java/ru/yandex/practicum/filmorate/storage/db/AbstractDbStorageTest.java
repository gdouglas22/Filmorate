package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest
@ActiveProfiles("db")
@Sql({"/schema.sql", "/sql/test-data.sql"})
public abstract class AbstractDbStorageTest {
}
