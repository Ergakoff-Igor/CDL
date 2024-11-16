package ru.docs.construction.catalogue.controller;

import org.junit.jupiter.api.DisplayName;
import ru.docs.construction.catalogue.controller.payload.UpdateActPayload;
import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;
import ru.docs.construction.catalogue.exceptions.InvalidActStatusException;
import ru.docs.construction.catalogue.service.ActService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ActRestController")
class ActRestControllerTest {

    @Mock
    ActService actService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ActRestController controller;

    @Test
    @DisplayName("getAct вернет акт по id")
    void getAct_ActExists_ReturnsAct() {
        // given
        var product = new Act(1L, "Январь", (short) 2024, "ЭМ", 1000d, ActStatus.ACCEPTED);
        doReturn(Optional.of(product)).when(this.actService).findAct(1L);

        // when
        var result = this.controller.getAct(1);

        // then
        assertEquals(product, result);
    }

    @Test
    @DisplayName("getAct выбросит NoSuchElementException с сообщением об ошибке")
    void getAct_ActDoesNotExist_ThrowsNoSuchElementException() {
        // given

        // when
        var exception = assertThrows(NoSuchElementException.class, () -> this.controller.getAct(1L));

        // then
        assertEquals("catalogue.errors.act.not_found", exception.getMessage());
    }

    @Test
    @DisplayName("getAct вернет акт")
    void findAct_ReturnsAct() {
        // given
        var act = new Act(1L, "Январь", (short) 2024, "ЭМ", 1000d, ActStatus.ACCEPTED);

        // when
        var result = this.controller.findAct(act);

        // then
        assertEquals(act, result);
    }

    @Test
    @DisplayName("updateAct изменит акт и вернет статус NO_CONTENT")
    void updateAct_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var payload = new UpdateActPayload("Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC);
        var bindingResult = new MapBindingResult(Map.of(), "payload");

        // when
        var result = this.controller.updateAct(1, payload, bindingResult);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).updateAct(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC);
    }

    @Test
    @DisplayName("updateAct вернет статус BAD_REQUEST")
    void updateAct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new UpdateActPayload(" ", null, " ", 2000d, ActStatus.CHECKING_QC);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));

        // when
        var exception = assertThrows(BindException.class, () -> this.controller.updateAct(1, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.actService);
    }

    @Test
    @DisplayName("updateAct вернет статус BAD_REQUEST")
    void updateAct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new UpdateActPayload(" ", null, " ", 2000d, ActStatus.CHECKING_QC);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));

        // when
        var exception = assertThrows(BindException.class, () -> this.controller.updateAct(1, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.actService);
    }

    @Test
    @DisplayName("turnActStatus изменит статус акта на correction и вернет статус noContent")
    void turnActStatus_RequestUpdateActStatusToCorrection_ReturnsNoContent() {
        // given
        long actId = 1L;
        String actStatus = "correction";

        // when
        var result = this.controller.turnActStatus(actId, actStatus);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).updateActStatus(1L, ActStatus.CORRECTION);
    }

    @Test
    @DisplayName("turnActStatus изменит статус акта на checkingQC и вернет статус noContent")
    void turnActStatus_RequestUpdateActStatusToCheckingQc_ReturnsNoContent() {
        // given
        long actId = 1L;
        String actStatus = "checkingQC";

        // when
        var result = this.controller.turnActStatus(actId, actStatus);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).updateActStatus(1L, ActStatus.CHECKING_QC);
    }

    @Test
    @DisplayName("turnActStatus изменит статус акта на checkingPtd и вернет статус noContent")
    void turnStatusToCorrection_RequestUpdateActStatusToCheckingPtd_ReturnsNoContent() {
        // given
        long actId = 1L;
        String actStatus = "checkingPtd";

        // when
        var result = this.controller.turnActStatus(actId, actStatus);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).updateActStatus(1L, ActStatus.CHECKING_PTD);
    }

    @Test
    @DisplayName("turnActStatus изменит статус акта на checkingBD и вернет статус noContent")
    void turnStatusToCorrection_RequestUpdateActStatusToCheckingBdc_ReturnsNoContent() {
        // given
        long actId = 1L;
        String actStatus = "checkingBD";

        // when
        var result = this.controller.turnActStatus(actId, actStatus);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).updateActStatus(1L, ActStatus.CHECKING_BD);
    }

    @Test
    @DisplayName("turnActStatus изменит статус акта на accepted и вернет статус noContent")
    void turnStatusToCorrection_RequestUpdateActStatusToAccepted_ReturnsNoContent() {
        // given
        long actId = 1L;
        String actStatus = "accepted";

        // when
        var result = this.controller.turnActStatus(actId, actStatus);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).updateActStatus(1L, ActStatus.ACCEPTED);
    }

    @Test
    @DisplayName("turnActStatus выбросит исключение InvalidActStatusException")
    void turnStatusToCorrection_RequestUpdateInvalidActStatus_ThrowsInvalidActStatusException() {
        // given
        long actId = 1L;
        String actStatus = "invalid";

        // when
        var exception = assertThrows(InvalidActStatusException.class, () -> this.controller.turnActStatus(actId, actStatus));

        // then
        assertEquals("catalogue.errors.act.bad_request", exception.getMessage());
        verifyNoInteractions(this.actService);
    }


    @Test
    @DisplayName("deleteAct удалит акт и вернет статус noContent")
    void deleteAct_ReturnsNoContent() {
        // given

        // when
        var result = this.controller.deleteAct(1);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.actService).deleteAct(1L);
    }

    @Test
    @DisplayName("handleNoSuchElementException перехватит исключение NoSuchElementException исключение и вернет статус NOT_FOUND")
    void handleNoSuchElementException_ReturnsNotFound() {
        // given
        var exception = new NoSuchElementException("error_code");
        var locale = Locale.of("ru");

        doReturn("error details").when(this.messageSource)
                .getMessage("error_code", new Object[0], "error_code", Locale.of("ru"));

        // when
        var result = this.controller.handleNoSuchElementException(exception, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getBody().getStatus());
        assertEquals("error details", result.getBody().getDetail());

        verifyNoInteractions(this.actService);
    }

    @Test
    @DisplayName("handleRuntimeException перехватит исключение InvalidActStatusException исключение и вернет статус BAD_REQUEST")
    void handleInvalidActStatusException_ReturnsBadRequest() {
        // given
        var exception = new InvalidActStatusException("error_code");
        var locale = Locale.of("ru");

        doReturn("error details").when(this.messageSource)
                .getMessage("error_code", new Object[0], "error_code", Locale.of("ru"));

        // when
        var result = this.controller.handleRuntimeException(exception, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getBody().getStatus());
        assertEquals("error details", result.getBody().getDetail());

        verifyNoInteractions(this.actService);
    }
}