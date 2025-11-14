// src/test/java/ru/yandex/practicum/filmorate/controller/FilmControllerIT.java
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/sql/test-genres.sql", "/sql/test-mpa.sql", "/sql/test-users.sql"})
class FilmControllerIT {

    @Autowired MockMvc mvc;

    @Test
    void create_and_get_ok() throws Exception {
        String body = """
          {
            "name":"nisi eiusmod",
            "description":"adipisicing",
            "releaseDate":"1967-03-25",
            "duration":100,
            "mpa":{"id":2},
            "genres":[{"id":2},{"id":6}]
          }
        """;

        // POST /films (ids DTO)
        var id = mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.mpa.id").value(2))
                .andExpect(jsonPath("$.genres[*].id", containsInAnyOrder(2,6)))
                .andReturn();

        String idStr = id.getResponse().getContentAsString().replaceAll(".*\"id\":(\\d+).*","$1");

        // GET /films/{id} (verbose DTO)
        mvc.perform(get("/films/"+idStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mpa.name").value(anyOf(is("PG"), is("PG-13"), is("PG-13")))) // DisplayName
                .andExpect(jsonPath("$.genres[?(@.id==2)].name").value(hasItem("Драма")));
    }
}
