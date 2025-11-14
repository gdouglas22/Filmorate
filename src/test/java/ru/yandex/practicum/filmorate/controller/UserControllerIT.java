// src/test/java/ru/yandex/practicum/filmorate/controller/UserControllerIT.java
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/sql/test-users.sql"})
class UserControllerIT {

    @Autowired MockMvc mvc;

    @Test
    void addFriend_oneWay_ok() throws Exception {
        // PUT /users/2/friends/1
        mvc.perform(put("/users/2/friends/1"))
                .andExpect(status().isNoContent());

        // user2 has 1 friend
        mvc.perform(get("/users/2/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // user1 has 0 friends
        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
