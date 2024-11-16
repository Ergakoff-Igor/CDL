package ru.docs.construction.manager.controller;

import org.junit.jupiter.api.DisplayName;
import ru.docs.construction.manager.entity.Act;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.docs.construction.manager.entity.ActStatus;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
@DisplayName("Интеграционные тесты ActController")
class ActControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("getAct выбросит ошибку доступа - статус 403")
    void getAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/1")
                .with(user("i.ergakov"));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @DisplayName("getActEditPage перенаправит на страницу редактирования акта")
    void getActEditPage_ActExists_ReturnsActEditPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/1/edit")
                .with(user("i.ergakov").roles("CONTRACTOR"));

        WireMock.stubFor(WireMock.get("/catalogue-api/acts/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "month": "Февраль",
                            "year": 2024,
                            "section": "ЭМ",
                            "price":  3000,
                            "actStatus": "CHECKING_PTD"
                        }
                        """)));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/acts/edit"),
                        model().attribute("act", new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD))
                );
    }

    @Test
    @DisplayName("getActEditPage выбросит исключение NoSuchElementException")
    void getActEditPage_ActDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/1/edit")
                .with(user("i.ergakov").roles("CONTRACTOR"));

        WireMock.stubFor(WireMock.get("/catalogue-api/acts/1")
                .willReturn(WireMock.notFound()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Акт не найден")
                );
    }

    @Test
    @DisplayName("getActEditPage выбросит ошибку доступа - статус 403")
    void getActEditPage_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/1/edit")
                .with(user("i.ergakov"));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @DisplayName("updateAct выбросит исключение NoSuchElementException")
    void updateAct_ActDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/1/edit")
                .param("month", "Февраль")
                .param("year", "2024")
                .param("section", "ЭМ")
                .param("price", "3000")
                .with(user("i.ergakov").roles("CONTRACTOR"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/acts/1")
                .willReturn(WireMock.notFound()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Акт не найден")
                );
    }

    @Test
    @DisplayName("updateAct выбросит ошибку доступа - статус 403")
    void updateAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/1/edit")
                .param("month", "Февраль")
                .param("year", "2024")
                .param("section", "ЭМ")
                .param("price", "3000")
                .with(user("i.ergakov"))
                .with(csrf());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @DisplayName("deleteAct выбросит исключение NoSuchElementException")
    void deleteAct_ActDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/1/delete")
                .with(user("i.ergakov").roles("CONTRACTOR"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/acts/1")
                .willReturn(WireMock.notFound()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Акт не найден")
                );
    }

    @Test
    @DisplayName("deleteAct выбросит ошибку доступа - статус 403")
    void deleteAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/1/delete")
                .with(user("i.ergakov"))
                .with(csrf());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}