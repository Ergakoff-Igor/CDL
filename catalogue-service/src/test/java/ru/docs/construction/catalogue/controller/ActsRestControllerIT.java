package ru.docs.construction.catalogue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты ActsRestController")
class ActsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("findActs отфильтрует акты по разделу проекта и вернет список актов")
    void findActs_ReturnsActsList() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/acts")
                .param("filter", "ЭМ")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {"id": 1, "month": "Февраль", "year": 2024, "section": "ЭМ", "price":  3000, "actStatus": "CHECKING_PTD"},
                                    {"id": 3, "month": "Январь", "year": 2024, "section": "ЭМ", "price":  2000, "actStatus": "ACCEPTED"}
                                ]""")
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("findActs вернет статус Forbidden")
    void findActs_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/acts")
                .param("filter", "ЭМ")
                .with(jwt());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @DisplayName("createAct вернет список ошибок валидации")
    void createAct_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/acts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "month": null,
                          "year": null,
                          "section": null,
                          "price":  5000
                          }""")
                .locale(Locale.of("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "errors": [
                                        "Месяц отчетного периода должен быть указан",
                                        "Год отчетного периода должен быть указан",
                                        "Раздел проекта должен быть указан"
                                    ]
                                }"""));
    }

    @Test
    @DisplayName("createAct вернет статус Forbidden")
    void createAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"month": "Февраль", "year": 2024, "section": "ЭМ", "price":  3000}""")
                .locale(Locale.of("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}