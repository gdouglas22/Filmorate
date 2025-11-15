package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "db"})
@Sql({"/schema.sql", "/sql/test-data.sql"})
public abstract class AbstractControllerIT {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    protected ResultActions postJson(String uriTemplate, Object body, Object... uriVariables) throws Exception {
        return mvc.perform(
                post(uriTemplate, uriVariables)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(body))
        );
    }

    protected ResultActions putJson(String uriTemplate, Object body, Object... uriVariables) throws Exception {
        return mvc.perform(
                put(uriTemplate, uriVariables)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(body))
        );
    }

    protected int extractId(String json) throws JsonProcessingException {
        return objectMapper.readTree(json).get("id").asInt();
    }

    protected <T> T parseResponse(String json, Class<T> targetClass) throws JsonProcessingException {
        return objectMapper.readValue(json, targetClass);
    }

    private String asJson(Object body) throws JsonProcessingException {
        if (body instanceof String str) {
            return str;
        }
        return objectMapper.writeValueAsString(body);
    }
}
