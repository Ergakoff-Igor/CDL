package ru.docs.construction.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import ru.docs.construction.catalogue.controller.payload.UpdateActPayload;
import ru.docs.construction.catalogue.entity.Act;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.docs.construction.catalogue.entity.ActStatus;
import ru.docs.construction.catalogue.exceptions.InvalidActStatusException;
import ru.docs.construction.catalogue.service.ActService;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/acts/{actId:\\d+}")
@Tag(name = "Контроллер акта", description = "Управляет функциями акта")
public class ActRestController {

    private final ActService actService;

    private final MessageSource messageSource;

    @ModelAttribute("act")
    public Act getAct(@PathVariable("actId") long actId) {
        return this.actService.findAct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @GetMapping
    @Operation(summary = "Поиск акта",
            description = "Позволяет вернуть акт из запроса при помощи @ModelAttribute",
            security = @SecurityRequirement(name = "keycloak")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    headers = @Header(name = "Content-Type", description = "Тип данных"),
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            type = "object",
                                            properties = {
                                                    @StringToClassMapItem(key = "id", value = Long.class),
                                                    @StringToClassMapItem(key = "month", value = String.class),
                                                    @StringToClassMapItem(key = "year", value = Short.class),
                                                    @StringToClassMapItem(key = "section", value = String.class),
                                                    @StringToClassMapItem(key = "price", value = Double.class),
                                                    @StringToClassMapItem(key = "actStatus", value = ActStatus.class)
                                            }
                                    )
                            )
                    }),
            @ApiResponse(responseCode = "404", description = "Акт не найден", content = {@Content(schema = @Schema())})
    })
    public Act findAct(@ModelAttribute("act") Act act) {
        return act;
    }

    @PatchMapping
    @Operation(summary = "Изменение акта",
            description = "Позволяет изменить акт",
            security = @SecurityRequirement(name = "keycloak"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "month", value = String.class),
                                            @StringToClassMapItem(key = "year", value = Short.class),
                                            @StringToClassMapItem(key = "section", value = String.class),
                                            @StringToClassMapItem(key = "price", value = Double.class),
                                            @StringToClassMapItem(key = "actStatus", value = ActStatus.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Акт изменен", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "400", description = "Ошибки валидации", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "404", description = "Акт не найден", content = {@Content(schema = @Schema())})
            })
    public ResponseEntity<?> updateAct(@PathVariable("actId") long actId,
                                       @Valid @RequestBody UpdateActPayload payload,
                                       BindingResult bindingResult) throws BindException {
        ResponseEntity<?> result;
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.actService.updateAct(actId, payload.month(), payload.year(), payload.section(), payload.price(), payload.actStatus());
            result = ResponseEntity.noContent()
                    .build();
        }
        return result;
    }

    @PatchMapping("/{status}")
    @Operation(summary = "Изменение статуса акта",
            description = "Позволяет изменить статус акта",
            security = @SecurityRequirement(name = "keycloak"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Статус акта изменен", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "400", description = "Ошибки валидации", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "404", description = "Акт не найден", content = {@Content(schema = @Schema())})
            })
    public ResponseEntity<?> turnActStatus(@PathVariable("actId") long actId, @PathVariable("status") String actStatus) {

        switch (actStatus) {
            case "correction" -> this.actService.updateActStatus(actId, ActStatus.CORRECTION);
            case "checkingQC" -> this.actService.updateActStatus(actId, ActStatus.CHECKING_QC);
            case "checkingPtd" -> this.actService.updateActStatus(actId, ActStatus.CHECKING_PTD);
            case "checkingBD" -> this.actService.updateActStatus(actId, ActStatus.CHECKING_BD);
            case "accepted" -> this.actService.updateActStatus(actId, ActStatus.ACCEPTED);
            default -> throw new InvalidActStatusException("catalogue.errors.act.bad_request");
        }

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping
    @Operation(summary = "Удаление акта",
            description = "Позволяет удалить акт",
            security = @SecurityRequirement(name = "keycloak"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Акт удален", content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "404", description = "Акт не найден", content = {@Content(schema = @Schema())})
            })
    public ResponseEntity<Void> deleteAct(@PathVariable("actId") long actId) {
        this.actService.deleteAct(actId);
        return ResponseEntity.noContent()
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception,
                                                                      Locale locale) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                        Objects.requireNonNull(this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale))));
    }

    @ExceptionHandler(InvalidActStatusException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(InvalidActStatusException exception,
                                                                Locale locale) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        Objects.requireNonNull(this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale))));
    }
}
