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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты ActRestController")
class ActRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("findAct вернёт акт по id")
    void findAct_ActExists_ReturnsAct() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/acts/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                                                {
                                                                    "id": 1,
                                                                    "month": "Февраль",
                                                                    "year": 2024,
                                                                    "section": "ЭМ",
                                                                    "actStatus": "CHECKING_PTD"
                                                                }
                                """)
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("findAct выбросит NoSuchElementException")
    void findAct_ActDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/acts/5")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("findAct вернет статус Forbidden")
    void findAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/acts/1")
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
    @Sql("/sql/acts.sql")
    @DisplayName("updateAct изменит акт, вернет статус noContent")
    void updateAct_RequestIsValid_ReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "month": "Май",
                            "year": 2024,
                            "section": "ЭМ",
                            "price":  5000
                        }""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("updateAct вернет статус BadRequest и список ошибок валидации")
    void updateAct_RequestIsNotValid_ReturnsBadRequest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "month": null,
                            "year": null,
                            "section": null,
                            "price":  5000
                        }""")
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
                                }""")
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("updateAct вернет статус NotFound")
    void updateAct_ActDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/5")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "month": "Май",
                            "year": 2024,
                            "section": "ЭМ",
                            "price":  5000
                        }""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @DisplayName("updateAct вернет статус Forbidden")
    void updateAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "month": "Май",
                            "year": 2024,
                            "section": "ЭМ",
                            "price":  5000
                        }""")
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
    @Sql("/sql/acts.sql")
    @DisplayName("turnActStatus изменит статус акта, вернет статус noContent")
    void turnActStatus_RequestIsValid_ReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/1/correction")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("turnActStatus вернет статус BadRequest")
    void turnActStatus_RequestIsInvalid_InvalidActStatusException() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/1/invalid")
                .locale(Locale.of("ru"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("turnActStatus вернет статус NotFound")
    void turnActStatus_ActDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/5/correction")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @DisplayName("turnActStatus вернет статус Forbidden")
    void turnActStatus_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/acts/1/correction")
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
    @Sql("/sql/acts.sql")
    @DisplayName("deleteAct изменит статус акта, вернет статус noContent")
    void deleteAct_ActExists_ReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/acts/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/acts.sql")
    @DisplayName("deleteAct вернет статус NotFound")
    void deleteAct_ActDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/acts/5")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @DisplayName("deleteAct вернет статус Forbidden")
    void deleteAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/acts/1")
                .with(jwt());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}