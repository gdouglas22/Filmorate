package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    private User validUser() {
        User u = new User();
        u.setEmail("user@example.com");
        u.setLogin("userlogin");
        u.setName("Имя");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return u;
    }

    @Test
    void createUser_ok_withMinimalValidData_setsIdAndStores() {
        User u = validUser();

        User saved = controller.createUser(u);

        assertNotNull(saved.getId());
        assertEquals("user@example.com", saved.getEmail());
        assertEquals("userlogin", saved.getLogin());
        assertEquals("Имя", saved.getName());
    }

    @Test
    void createUser_setsNameToLogin_whenNameBlank() {
        User u = validUser();
        u.setName("  ");

        User saved = controller.createUser(u);

        assertEquals(saved.getLogin(), saved.getName(), "Если name пустое — подставляем login");
    }

    @Test
    void createUser_fails_whenEmailNull() {
        User u = validUser();
        u.setEmail(null);

        assertThrows(ValidationException.class, () -> controller.createUser(u),
                "Ожидаем ошибку: почта обязательна");
    }

    @Test
    void createUser_fails_whenEmailNoAt() {
        User u = validUser();
        u.setEmail("invalid.email.example.com");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> controller.createUser(u)
        );
        assertEquals("Ошибка валидации пользователя", ex.getMessage());
    }
    @Test
    void createUser_fails_whenLoginBlank() {
        User u = validUser();
        u.setLogin("  ");

        assertThrows(ValidationException.class, () -> controller.createUser(u));
    }

    @Test
    void createUser_fails_whenLoginContainsSpaces() {
        User u = validUser();
        u.setLogin("bad login");

        assertThrows(ValidationException.class, () -> controller.createUser(u),
                "Логин с пробелом должен быть отклонён");
    }

    @Test
    void createUser_fails_whenBirthdayNull() {
        User u = validUser();
        u.setBirthday(null);

        assertThrows(ValidationException.class, () -> controller.createUser(u));
    }

    @Test
    void createUser_fails_whenBirthdayInFuture() {
        User u = validUser();
        u.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.createUser(u));
    }

    @Test
    void createUser_fails_onEmptyRequest() {
        User empty = new User();
        assertThrows(ValidationException.class, () -> controller.createUser(empty),
                "Пустой запрос должен быть отклонён на первой же валидации");
    }

    @Test
    void update_fails_whenIdMissing() {
        User u = validUser();
        u.setId(0);
        assertThrows(ValidationException.class, () -> controller.update(u));
    }

    @Test
    void update_fails_whenUserNotFound() {
        User u = validUser();
        u.setId(999L);
        assertThrows(ValidationException.class, () -> controller.update(u));
    }

    @Test
    void update_updatesOnlyProvidedFields_andValidates() {
        User saved = controller.createUser(validUser());

        User patch = new User();
        patch.setId(saved.getId());
        patch.setName("Новое имя");
        patch.setLogin("bad login");

        assertThrows(ValidationException.class, () -> controller.update(patch));
    }

    @Test
    void getAll_empty() {
        Collection<User> result = controller.getAll();

        assertTrue(result.isEmpty(), "Ожидаем пустой список пользователей");
    }

    @Test
    void getAll_notEmpty() {
        User u = validUser();
        User u2 = validUser();

        controller.createUser(u);
        u2.setEmail("another@mail.ru");
        controller.createUser(u2);

        Collection<User> result = controller.getAll();
        assertEquals(2, result.size(), "Должны получить 2 пользователя");
    }
}