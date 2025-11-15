package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FilmControllerIT extends AbstractControllerIT {

    @Test
    @DisplayName("GET /films возвращает seed-данные")
    void getAllFilms_seedData() throws Exception {
        mvc.perform(get("/films")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5))).andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].name").value("Inception")).andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("GET /films/popular сортирует фильмы по количеству лайков")
    void getPopularFilms_sortedByLikes() throws Exception {
        mvc.perform(get("/films/popular").param("count", "3")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3))).andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[1].id").value(4)).andExpect(jsonPath("$[2].id").value(5));
    }

    @Test
    @DisplayName("Должен создавать фильм и возвращать его по id")
    void create_and_get_ok() throws Exception {
        String newFilmJson = """
                {
                  "name": "New Film",
                  "description": "Test description",
                  "releaseDate": "2000-01-01",
                  "duration": 120,
                  "mpa": { "id": 1 },
                  "genres": [
                    { "id": 1 },
                    { "id": 2 }
                  ]
                }
                """;

        String createdJson = mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(newFilmJson)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.name").value("New Film")).andReturn().getResponse().getContentAsString();

        int id = extractId(createdJson);

        mvc.perform(get("/films/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.name").value("New Film")).andExpect(jsonPath("$.mpa.id").value(1)).andExpect(jsonPath("$.genres", hasSize(2))).andExpect(jsonPath("$.likes").value(0));
    }

    @Test
    @DisplayName("Должен добавлять и удалять лайк фильму")
    void add_and_remove_like() throws Exception {
        String newFilmJson = """
                {
                  "name": "Liked Film",
                  "description": "Film for like test",
                  "releaseDate": "2001-01-01",
                  "duration": 90,
                  "mpa": { "id": 1 },
                  "genres": [ { "id": 1 } ]
                }
                """;

        String createdJson = mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(newFilmJson)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        int id = extractId(createdJson);

        mvc.perform(put("/films/{id}/like/{userId}", id, 1L)).andExpect(status().isNoContent());

        mvc.perform(get("/films/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.likes", is(1)));

        mvc.perform(delete("/films/{id}/like/{userId}", id, 1L)).andExpect(status().isNoContent());

        mvc.perform(get("/films/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.likes", is(0)));
    }

    @Test
    @DisplayName("Должен вернуть 400 при некорректных данных фильма")
    void create_invalid_film_bad_request() throws Exception {
        String invalidFilmJson = """
                {
                  "name": "",
                  "description": "Too old film",
                  "releaseDate": "1800-01-01",
                  "duration": -1,
                  "mpa": { "id": 1 }
                }
                """;

        mvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(invalidFilmJson)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /films/{id} обновляет фильм по id из пути")
    void updateFilm_byPathVariable_ok() throws Exception {
        String updateJson = """
                {
                  "name": "Inception Updated",
                  "description": "Updated description",
                  "releaseDate": "2010-07-16",
                  "duration": 150,
                  "mpa": { "id": 4 },
                  "genres": [
                    { "id": 4 },
                    { "id": 6 }
                  ]
                }
                """;

        mvc.perform(put("/films/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(updateJson)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Inception Updated"));

        mvc.perform(get("/films/{id}", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Inception Updated")).andExpect(jsonPath("$.duration").value(150));
    }

    @Test
    @DisplayName("PUT /films (body) обновляет фильм по id из тела")
    void updateFilm_withBody_ok() throws Exception {
        String updateJson = """
                {
                  "id": 2,
                  "name": "Finding Nemo Updated",
                  "description": "Updated underwater adventure",
                  "releaseDate": "2003-05-30",
                  "duration": 110,
                  "mpa": { "id": 1 },
                  "genres": [
                    { "id": 3 }
                  ]
                }
                """;

        mvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(updateJson)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(2)).andExpect(jsonPath("$.name").value("Finding Nemo Updated"));

        mvc.perform(get("/films/{id}", 2)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Finding Nemo Updated")).andExpect(jsonPath("$.duration").value(110));
    }

    @Test
    @DisplayName("PUT /films (body) возвращает 400, если id в теле отсутствует или некорректен")
    void updateFilm_withBody_missingOrInvalidId_badRequest() throws Exception {
        String noIdJson = """
                {
                  "name": "No Id Film",
                  "description": "Invalid update",
                  "releaseDate": "2000-01-01",
                  "duration": 100,
                  "mpa": { "id": 1 },
                  "genres": [ { "id": 1 } ]
                }
                """;

        mvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(noIdJson)).andExpect(status().isBadRequest());

        String invalidIdJson = """
                {
                  "id": 0,
                  "name": "Invalid Id Film",
                  "description": "Invalid update",
                  "releaseDate": "2000-01-01",
                  "duration": 100,
                  "mpa": { "id": 1 },
                  "genres": [ { "id": 1 } ]
                }
                """;

        mvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(invalidIdJson)).andExpect(status().isBadRequest());
    }

}
