package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GenreControllerIT extends AbstractControllerIT {

    @Test
    @DisplayName("GET /genres возвращает все жанры из тестовой базы")
    void getAllGenres_ok() throws Exception {
        mvc.perform(get("/genres")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(6))).andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].name").value("Комедия"));
    }

    @Test
    @DisplayName("GET /genres/{id} возвращает жанр по id")
    void getGenreById_ok() throws Exception {
        mvc.perform(get("/genres/3")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(3)).andExpect(jsonPath("$.name").value("Мультфильм"));
    }

    @Test
    @DisplayName("GET /genres/{id} возвращает 404 для несуществующего id")
    void getGenreById_notFound() throws Exception {
        mvc.perform(get("/genres/999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /genres/{id}/films возвращает фильмы жанра с расширенными данными")
    void getFilmsByGenre_ok() throws Exception {
        mvc.perform(get("/genres/{id}/films", 4)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].mpa.id").value(4)).andExpect(jsonPath("$[0].genres", hasSize(2))).andExpect(jsonPath("$[1].id").value(3)).andExpect(jsonPath("$[1].genres[0].name").value("Триллер"));
    }
}
