package ru.docs.construction.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import ru.docs.construction.catalogue.controller.payload.NewActPayload;
import ru.docs.construction.catalogue.entity.Act;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.docs.construction.catalogue.entity.ActStatus;
import ru.docs.construction.catalogue.service.ActService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/acts")
@Tag(name = "Контроллер списка актов", description = "Управляет функциями списка актов, а так-же позволяет создать новый акт")
public class ActsRestController {

    private final ActService actService;

    @GetMapping
    @Operation(summary = "Поиск актов",
            description = "Позволяет найти список актов по фильтру, либо список всех актов",
            security = @SecurityRequirement(name = "keycloak"))
    public Iterable<Act> findActs(@RequestParam(name = "filter", required = false) String filter) {
        return this.actService.findAllActs(filter);
    }

    @PostMapping
    @Operation(summary = "Создание акта",
            description = "Позволяет создать новый акт",
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
                                            @StringToClassMapItem(key = "price", value = Double.class)
                                    }
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
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
                            }
                    )
            })
    public ResponseEntity<?> createAct(@Valid @RequestBody NewActPayload payload,
                                       BindingResult bindingResult,
                                       UriComponentsBuilder uriComponentsBuilder)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Act act = this.actService.createAct(payload.month(), payload.year(), payload.section(), payload.price());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/catalogue-api/acts/{actId}")
                            .build(Map.of("actId", act.getId())))
                    .body(act);
        }
    }
}
