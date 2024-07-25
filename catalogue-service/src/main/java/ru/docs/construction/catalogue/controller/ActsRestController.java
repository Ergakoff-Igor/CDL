package ru.docs.construction.catalogue.controller;

import ru.docs.construction.catalogue.controller.payload.NewActPayload;
import ru.docs.construction.catalogue.entity.Act;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.docs.construction.catalogue.service.ActService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/acts")
public class ActsRestController {

    private final ActService actService;

    @GetMapping
    public List<Act> findActs() {
        return this.actService.findAllActs();
    }

    @PostMapping
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
