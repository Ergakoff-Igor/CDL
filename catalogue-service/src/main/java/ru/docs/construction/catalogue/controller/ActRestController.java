package ru.docs.construction.catalogue.controller;

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
public class ActRestController {

    private final ActService actService;

    private final MessageSource messageSource;

    @ModelAttribute("act")
    public Act getAct(@PathVariable("actId") long actId) {
        return this.actService.findAct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @GetMapping
    public Act findAct(@ModelAttribute("act") Act act) {
        return act;
    }

    @PatchMapping
    public ResponseEntity<?> updateAct(@PathVariable("actId") long actId,
                                       @Valid @RequestBody UpdateActPayload payload,
                                       BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.actService.updateAct(actId, payload.month(), payload.year(), payload.section(), payload.price(), payload.actStatus());
            return ResponseEntity.noContent()
                    .build();
        }
    }

    @PatchMapping("/{status}")
    public ResponseEntity<?> turnStatusToCorrection(@PathVariable("actId") long actId, @PathVariable("status") String actStatus) {

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
