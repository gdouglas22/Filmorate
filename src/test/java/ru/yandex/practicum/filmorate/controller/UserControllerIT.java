package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIT extends AbstractControllerIT {

    @Test
    @DisplayName("GET /users возвращает seed-данные")
    void getAllUsers_ok() throws Exception {
        mvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(4))).andExpect(jsonPath("$[0].login", is("alice"))).andExpect(jsonPath("$[1].login", is("bob")));
    }

    @Test
    @DisplayName("GET /users/{id} возвращает пользователя")
    void getUserById_ok() throws Exception {
        mvc.perform(get("/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.login").value("alice")).andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("POST /users создаёт пользователя")
    void createUser_ok() throws Exception {
        String json = """
                  {
                    "email": "new@mail.com",
                    "login": "newuser",
                    "name": "New User",
                    "birthday": "2000-01-01"
                  }
                """;

        String created = postJson("/users", json).andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.login").value("newuser")).andReturn().getResponse().getContentAsString();

        int id = extractId(created);

        mvc.perform(get("/users/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("new@mail.com")).andExpect(jsonPath("$.login").value("newuser"));
    }

    @Test
    @DisplayName("POST /users возвращает 400 при неверных данных")
    void createUser_invalid_badRequest() throws Exception {
        String json = """
                  {
                    "email": "wrong-email",
                    "login": "user with space",
                    "birthday": "2035-01-01"
                  }
                """;

        postJson("/users", json).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /users/{id} обновляет пользователя по id из пути")
    void updateUser_byPathVariable_ok() throws Exception {
        String json = """
                  {
                    "email": "alice@path-update.com",
                    "login": "alice",
                    "name": "Alice Path",
                    "birthday": "1995-03-14"
                  }
                """;

        putJson("/users/{id}", json, 1).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.email").value("alice@path-update.com")).andExpect(jsonPath("$.name").value("Alice Path"));
    }

    @Test
    @DisplayName("PUT /users обновляет пользователя")
    void updateUser_ok() throws Exception {
        String json = """
                  {
                    "id": 1,
                    "email": "updated@example.com",
                    "login": "alice",
                    "name": "Alice Updated",
                    "birthday": "1995-03-14"
                  }
                """;

        putJson("/users", json).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("updated@example.com")).andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    @DisplayName("PUT /users/{id}/friends/{friendId} добавляет друга")
    void addFriend_ok() throws Exception {
        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", contains(2))); // только Bob

        mvc.perform(put("/users/1/friends/3")).andExpect(status().isNoContent());

        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", containsInAnyOrder(2, 3)));
    }

    @Test
    @DisplayName("DELETE /users/{id}/friends/{friendId} удаляет друга")
    void removeFriend_ok() throws Exception {
        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", contains(2)));

        mvc.perform(delete("/users/1/friends/2")).andExpect(status().isNoContent());

        mvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /users/{id}/friends/common/{otherId} возвращает общих друзей")
    void getCommonFriends_ok() throws Exception {
        mvc.perform(put("/users/3/friends/2")).andExpect(status().isNoContent());

        mvc.perform(get("/users/1/friends/common/3")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", contains(2)));
    }
}
