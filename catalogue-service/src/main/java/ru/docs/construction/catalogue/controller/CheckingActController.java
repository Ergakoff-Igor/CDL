package ru.docs.construction.catalogue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;
import ru.docs.construction.catalogue.exceptions.InvalidActStatusException;
import ru.docs.construction.catalogue.service.ActService;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
@RequestMapping(("catalogue-api/acts/{actId:\\d+}/{status}"))
@RequiredArgsConstructor
public class CheckingActController {
    private final ActService actService;

    private final MessageSource messageSource;

    @ModelAttribute("act")
    public Act act(@PathVariable("actId") long actId) {
        return this.actService.findAct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @PatchMapping
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
