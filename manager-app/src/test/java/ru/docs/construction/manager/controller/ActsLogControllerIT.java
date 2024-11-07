package ru.docs.construction.manager.controller;

import ru.docs.construction.manager.controller.payload.NewActPayload;
import ru.docs.construction.manager.entity.Act;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.docs.construction.manager.entity.ActStatus;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
class ActsLogControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getActList_ReturnsActsListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/list")
                .queryParam("filter", "ЭМ")
                .with(user("i.ergakov").roles("CONTRACTOR"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/acts"))
                .withQueryParam("filter", WireMock.equalTo("ЭМ"))
                .willReturn(WireMock.ok("""
                        [
                            {"id": 1, "month": "Февраль", "year": 2024, "section": "ЭМ", "price":  3000, "actStatus": "CHECKING_PTD"},
                            {"id": 3, "month": "Январь", "year": 2024, "section": "ЭМ", "price":  2000, "actStatus": "ACCEPTED"}
                        ]""").withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/acts/list"),
                        model().attribute("filter", "ЭМ"),
                        model().attribute("acts", List.of(
                                new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD),
                                new Act(3L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.ACCEPTED)
                        ))
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/acts"))
                .withQueryParam("filter", WireMock.equalTo("ЭМ")));
    }

    @Test
    void getActList_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/list")
                .queryParam("filter", "ЭМ")
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
      void getNewActPage_ReturnsActPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/create")
                .with(user("i.ergakov").roles("CONTRACTOR"));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/acts/new_act")
                );
    }

    @Test
    void getNewActPage_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/acts/create")
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
    void createAct_RequestIsValid_RedirectsToActPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/create")
                .param("month", "Февраль")
                .param("year", "2024")
                .param("section", "ЭМ")
                .param("price", "3000")
                .with(user("i.ergakov").roles("CONTRACTOR"))
                .with(csrf());

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/catalogue-api/acts"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                                    "Id": 1,
                                    "month": "Февраль",
                                    "year": 2024,
                                    "section": "ЭМ",
                                    "price":  3000,
                                    "actStatus": "CHECKING_QC"
                        }"""))
                .willReturn(WireMock.created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "Id": 1,
                                    "month": "Февраль",
                                    "year": 2024,
                                    "section": "ЭМ",
                                    "price":  3000,
                                    "actStatus": "CHECKING_QC"
                                }""")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        header().string(HttpHeaders.LOCATION, "/catalogue/acts/1")
                );

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/catalogue-api/acts"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                                    "month": "Февраль",
                                    "year": 2024,
                                    "section": "ЭМ",
                                    "price":  3000
                        }""")));
    }

    @Test
    void createAct_RequestIsInvalid_ReturnsNewActPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/create")
                .param("month", " ")
                .param("year", " ")
                .param("section", "ЭМ")
                .param("price", "3000")
                .with(user("i.ergakov").roles("CONTRACTOR"))
                .with(csrf());

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/catalogue-api/acts"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "month": null,
                            "year": null,
                            "section": "ЭМ",
                            "price":  5000
                        }"""))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2"]
                                }""")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("catalogue/acts/new_act"),
                        model().attribute("payload", new NewActPayload(null, null, "ЭМ", 5000d)),
                        model().attribute("errors", List.of("Ошибка 1", "Ошибка 2"))
                );

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/catalogue-api/acts"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "month": null,
                            "year": null,
                            "section": "ЭМ",
                            "price":  5000
                        }""")));
    }

    @Test
    void createAct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/acts/create")
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
}
