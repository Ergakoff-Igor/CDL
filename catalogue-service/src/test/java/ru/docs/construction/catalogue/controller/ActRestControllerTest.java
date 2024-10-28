package ru.docs.construction.catalogue.controller;

import ru.docs.construction.catalogue.controller.payload.UpdateActPayload;
import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;
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
class ActRestControllerTest {

    @Mock
    ActService actService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ActRestController controller;

    @Test
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
    void getAct_ActDoesNotExist_ThrowsNoSuchElementException() {
        // given

        // when
        var exception = assertThrows(NoSuchElementException.class, () -> this.controller.getAct(1L));

        // then
        assertEquals("catalogue.errors.act.not_found", exception.getMessage());
    }

    @Test
    void findAct_ReturnsAct() {
        // given
        var act = new Act(1L, "Январь", (short) 2024, "ЭМ", 1000d, ActStatus.ACCEPTED);

        // when
        var result = this.controller.findAct(act);

        // then
        assertEquals(act, result);
    }

    @Test
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
    void handleNoSuchElementException_ReturnsNotFound() {
        // given
        var exception = new NoSuchElementException("error_code");
        var locale = new Locale("ru");

        doReturn("error details").when(this.messageSource)
                .getMessage("error_code", new Object[0], "error_code", locale);

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
}