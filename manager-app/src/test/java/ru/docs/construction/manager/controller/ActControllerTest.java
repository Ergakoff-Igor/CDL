package ru.docs.construction.manager.controller;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import ru.docs.construction.manager.client.BadRequestException;
import ru.docs.construction.manager.client.ActsRestClient;
import ru.docs.construction.manager.controller.payload.UpdateActPayload;
import ru.docs.construction.manager.entity.Act;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import ru.docs.construction.manager.entity.ActStatus;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActControllerTest {

    @Mock
    ActsRestClient actsRestClient;

    @Mock
    MessageSource messageSource;

    @Mock
    OidcUser oidcUser;

    @Mock
    Model model;

    @InjectMocks
    ActController controller;

    @Test
    void act_ActExists_ReturnsAct() {
        // given
        var act = new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD);

        doReturn(Optional.of(act)).when(this.actsRestClient).findAct(1);

        // when
        var result = this.controller.act(1);

        // then
        assertEquals(act, result);

        verify(this.actsRestClient).findAct(1);
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    void act_ActDoesNotExist_ThrowsNoSuchElementException() {
        // given

        // when
        var exception = assertThrows(NoSuchElementException.class, () -> this.controller.act(1));

        // then
        assertEquals("catalogue.errors.act.not_found", exception.getMessage());

        verify(this.actsRestClient).findAct(1);
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    void getAct_ReturnsActPage() {
        // given

        // when
        var result = this.controller.getAct(this.oidcUser, this.model);

        // then
        assertEquals("catalogue/acts/act", result);

        verifyNoInteractions(this.actsRestClient);
    }

    @Test
    void getActEditPage_ReturnsActEditPage() {
        // given

        // when
        var result = this.controller.getActEditPage();

        // then
        assertEquals("catalogue/acts/edit", result);

        verifyNoInteractions(this.actsRestClient);
    }

    @Test
    void updateAct_RequestIsValid_RedirectsToActPage() {
        // given
        var act = new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD);
        var payload = new UpdateActPayload("Март", (short) 2024, "ЭМ", 4000d, ActStatus.CHECKING_PTD);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        // when
        var result = this.controller.updateAct(act, payload, model, response);

        // then
        assertEquals("redirect:/catalogue/acts/1", result);

        verify(this.actsRestClient).updateAct(1L, "Март", (short) 2024, "ЭМ", 4000d, ActStatus.CHECKING_PTD);
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    void updateAct_RequestIsInvalid_ReturnsActEditPage() {
        // given
        var act = new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD);
        var payload = new UpdateActPayload(null, null, null, 4000d, ActStatus.CHECKING_PTD);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2", "Ошибка 3")))
                .when(this.actsRestClient).updateAct(1L, null, null, null, 4000d, ActStatus.CHECKING_PTD);

        // when
        var result = this.controller.updateAct(act, payload, model, response);

        // then
        assertEquals("catalogue/acts/edit", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2", "Ошибка 3"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(this.actsRestClient).updateAct(1L, null, null, null, 4000d, ActStatus.CHECKING_PTD);
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    void deleteAct_RedirectsToActsListPage() {
        // given
        var act = new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD);

        // when
        var result = this.controller.deleteAct(act);

        // then
        assertEquals("redirect:/catalogue/acts/list", result);

        verify(this.actsRestClient).deleteAct(1L);
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    void turnActStatus_RequestIsValid_RedirectsToActsListPage() {
        // given
        var act = new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD);
        var actStatus = "accepted";

        // when
        var result = this.controller.turnActStatus(act, actStatus);

        // then
        assertEquals("redirect:/catalogue/acts/list", result);

        verify(this.actsRestClient).updateActStatus(1L, "accepted");
        verifyNoMoreInteractions(this.actsRestClient);
    }

    @Test
    void handleNoSuchElementException_Returns404ErrorPage() {
        // given
        var exception = new NoSuchElementException("error");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        var locale = Locale.of("ru");

        doReturn("Ошибка").when(this.messageSource)
                .getMessage("error", new Object[0], "error", Locale.of("ru"));

        // when
        var result = this.controller.handleNoSuchElementException(exception, model, response, locale);

        // then
        assertEquals("errors/404",  result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(this.messageSource).getMessage("error", new Object[0], "error", Locale.of("ru"));
        verifyNoMoreInteractions(this.messageSource);
        verifyNoInteractions(this.actsRestClient);
    }
}