package ru.docs.construction.catalogue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;
import ru.docs.construction.catalogue.controller.payload.NewActPayload;
import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;
import ru.docs.construction.catalogue.service.ActService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ActsRestController")
class ActsRestControllerTest {
    @Mock
    ActService actService;

    @InjectMocks
    ActsRestController controller;

    @Test
    @DisplayName("findActs отфильтрует акты по разделу проекта и вернет список актов")
    void findActs_ReturnsActsList() {
        // given
        var filter = "АК";

        doReturn(List.of(
                new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC),
                new Act(2L, "Февраль", (short) 2024, "АК", 1000d, ActStatus.ACCEPTED)))
                .when(this.actService).findAllActs("АК");

        // when
        var result = this.controller.findActs(filter);

        // then
        assertEquals(List.of(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC),
                new Act(2L, "Февраль", (short) 2024, "АК", 1000d, ActStatus.ACCEPTED)), result);
    }

    @Test
    @DisplayName("createAct создаст акт, вернет статус noContent")
    void createAct_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var payload = new NewActPayload("Январь", (short) 2024, "ЭМ", 2000d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .when(this.actService).createAct("Январь", (short) 2024, "ЭМ", 2000d);

        // when
        var result = this.controller.createAct(payload, bindingResult, uriComponentsBuilder);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/catalogue-api/acts/1"), result.getHeaders().getLocation());
        assertEquals(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC), result.getBody());

        verify(this.actService).createAct("Январь", (short) 2024, "ЭМ", 2000d);
        verifyNoMoreInteractions(this.actService);
    }

    @Test
    @DisplayName("createAct вернет статус BadRequest и список ошибок валидации")
    void createAct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new NewActPayload(" ", null, " ", 2000d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.createAct(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.actService);
    }

    @Test
    @DisplayName("createAct вернет статус BadRequest и список ошибок валидации")
    void createAct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new NewActPayload(" ", null, " ", 2000d);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.createAct(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.actService);
    }
}