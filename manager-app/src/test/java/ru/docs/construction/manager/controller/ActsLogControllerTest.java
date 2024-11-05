package ru.docs.construction.manager.controller;

import ru.docs.construction.manager.client.BadRequestException;
import ru.docs.construction.manager.client.ActsRestClient;
import ru.docs.construction.manager.controller.payload.NewActPayload;
import ru.docs.construction.manager.entity.Act;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import ru.docs.construction.manager.entity.ActStatus;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ActsLogController")
class ActsLogControllerTest {

    @Mock
    ActsRestClient actsRestClient;

    @InjectMocks
    ActsLogController controller;

    @Test
    void getActsList_ReturnsActsListPage() {
        // given
        var model = new ConcurrentModel();
        var filter = "ЭМ";

        var acts = LongStream.range(1, 4)
                .mapToObj(i -> new Act(i, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .toList();

        doReturn(acts).when(this.actsRestClient).findAllActs(filter);

        // when
        var result = this.controller.getActsList(model, filter);

        // then
        assertEquals("catalogue/acts/index", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(acts, model.getAttribute("acts"));
    }

    @Test
    void getNewActPage_ReturnsNewActPage() {
        // given

        // when
        var result = this.controller.getNewActPage();

        // then
        assertEquals("catalogue/acts/new_act", result);
    }

    @Test
    @DisplayName("createAct создаст новый акт и перенаправит на страницу акта")
    void createAct_RequestIsValid_ReturnsRedirectionToActPage() {
        // given
        var payload = new NewActPayload("Январь", (short) 2024, "ЭМ", 2000d);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .when(this.actsRestClient)
                .createAct("Январь", (short) 2024, "ЭМ", 2000d);

        // when
        var result = this.controller.createAct(payload, model, response);

        // then
        assertEquals("redirect:/catalogue/acts/1", result);

        verify(this.actsRestClient).createAct("Январь", (short) 2024, "ЭМ", 2000d);
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    @DisplayName("createAct вернёт страницу с ошибками, если запрос невалиден")
    void createAct_RequestIsInvalid_ReturnsActFormWithErrors() {
        // given
        var payload = new NewActPayload(" ", null, " ", 2000d);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(this.actsRestClient)
                .createAct(" ", null, " ", 2000d);

        // when
        var result = this.controller.createAct(payload, model, response);

        // then
        assertEquals("catalogue/acts/new_act", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(this.actsRestClient).createAct(" ", null, " ", 2000d);
        verifyNoMoreInteractions(this.actsRestClient);
    }
}