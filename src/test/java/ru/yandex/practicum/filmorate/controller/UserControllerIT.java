package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT extends AbstractControllerIT {

    private String readJson(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            return new String(
                    Objects.requireNonNull(is, "Resource not found: " + resourcePath).readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    @Test
    @DisplayName("GET /users возвращает seed-данные")
    void getAllUsers_ok() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].login", is("alice")))
                .andExpect(jsonPath("$[1].login", is("bob")));
    }

    @Test
    @DisplayName("GET /users/{id} возвращает пользователя")
    void getUserById_ok() throws Exception {
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("POST /users создаёт пользователя")
    void createUser_ok() throws Exception {
        String json = readJson("json/user_create_valid.json");

        String created = postJson("/users", json)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("newuser"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = extractId(created);

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.login").value("newuser"));
    }

    @Test
    @DisplayName("POST /users возвращает 400 при неверных данных")
    void createUser_invalid_badRequest() throws Exception {
        String json = readJson("json/user_create_invalid.json");

        postJson("/users", json)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /users/{id} обновляет пользователя по id из пути")
    void updateUser_byPathVariable_ok() throws Exception {
        String json = readJson("json/user_update_by_path.json");

        putJson("/users/{id}", json, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@path-update.com"))
                .andExpect(jsonPath("$.name").value("Alice Path"));
    }

    @Test
    @DisplayName("PUT /users обновляет пользователя")
    void updateUser_ok() throws Exception {
        String json = readJson("json/user_update_body.json");

        putJson("/users", json)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    @DisplayName("PUT /users/{id}/friends/{friendId} добавляет друга")
    void addFriend_ok() throws Exception {
        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", contains(2))); // только Bob

        mvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(2, 3)));
    }

    @Test
    @DisplayName("DELETE /users/{id}/friends/{friendId} удаляет друга")
    void removeFriend_ok() throws Exception {
        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", contains(2)));

        mvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /users/{id}/friends/common/{otherId} возвращает общих друзей")
    void getCommonFriends_ok() throws Exception {
        mvc.perform(put("/users/3/friends/2"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", contains(2)));
    }
}