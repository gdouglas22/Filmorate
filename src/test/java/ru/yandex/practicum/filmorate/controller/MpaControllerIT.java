package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MpaControllerIT extends AbstractControllerIT {

    @Test
    @DisplayName("GET /mpa возвращает все рейтинги")
    void getAllMpa_ok() throws Exception {
        mvc.perform(get("/mpa")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5))).andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].name").value("G"));
    }

    @Test
    @DisplayName("GET /mpa/{id} возвращает рейтинг по id")
    void getMpaById_ok() throws Exception {
        mvc.perform(get("/mpa/3")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(3)).andExpect(jsonPath("$.name").value("PG-13"));
    }

    @Test
    @DisplayName("GET /mpa/{id} → 404 для некорректного id")
    void getMpaById_notFound() throws Exception {
        mvc.perform(get("/mpa/999")).andExpect(status().isNotFound());
    }
}
