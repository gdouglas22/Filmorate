package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController controller;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        controller = new UserController(userService);
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
    void getAll_empty_returnsEmptyList() {
        when(userService.getAll()).thenReturn(List.of());

        var result = controller.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).getAll();
    }

    @Test
    void getAll_notEmpty_returnsList() {
        when(userService.getAll()).thenReturn(List.of(validUser(), validUser()));

        var result = controller.getAll();

        assertEquals(2, result.size());
        verify(userService, times(1)).getAll();
    }

    @Test
    void createUser_ok_delegatesToService_andReturnsSaved() {
        var toCreate = validUser();
        var saved = validUser();
        saved.setId(123L);

        when(userService.create(toCreate)).thenReturn(saved);

        var result = controller.createUser(toCreate);

        assertEquals(123L, result.getId());
        assertEquals("user@example.com", result.getEmail());
        verify(userService, times(1)).create(toCreate);
    }

    @Test
    void createUser_bubblesValidationException_fromService() {
        var bad = new User(); // заведомо некорректный
        when(userService.create(bad)).thenThrow(new ValidationException("Ошибка валидации пользователя"));

        assertThrows(ValidationException.class, () -> controller.createUser(bad));
        verify(userService, times(1)).create(bad);
    }

    @Test
    void update_ok_delegatesToService_andReturnsUpdated() {
        var patch = validUser();
        patch.setId(77L);
        patch.setName("Новое имя");

        when(userService.update(patch)).thenReturn(patch);

        var updated = controller.update(patch);

        assertEquals(77L, updated.getId());
        assertEquals("Новое имя", updated.getName());
        verify(userService, times(1)).update(patch);
    }

    @Test
    void update_bubblesValidationException_fromService() {
        var patch = validUser();
        patch.setId(0L);
        when(userService.update(patch)).thenThrow(new ValidationException("Id должен быть указан"));

        assertThrows(ValidationException.class, () -> controller.update(patch));
        verify(userService, times(1)).update(patch);
    }
}
