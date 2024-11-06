package ru.docs.construction.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
class ActsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/acts.sql")
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
    void findProducts_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
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
    @Sql("/sql/acts.sql")
    void createAct_RequestIsValid_ReturnsNewAct() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/acts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"month": "Февраль", "year": 2024, "section": "ЭМ", "price":  3000}""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_actslog")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/acts/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "Id": 1,
                                    "month": "Февраль",
                                    "year": 2024,
                                    "section": "ЭМ",
                                    "price":  3000,
                                    "actStatus": "CHECKING_QC"
                                }"""));
    }

    @Test
    void createAct_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/acts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {   "month": null,
                            "year": null,
                            "section": null,
                            "price":  5000}""")
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