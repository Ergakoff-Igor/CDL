package ru.docs.construction.manager.controller;

import ru.docs.construction.manager.controller.payload.UpdateProductPayload;
import ru.docs.construction.manager.entity.Act;
import ru.docs.construction.manager.entity.ActStatus;
import ru.docs.construction.manager.service.ActService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
@RequestMapping(("catalogue/acts/{actId:\\d+}/{status}"))
@RequiredArgsConstructor
public class CheckingActController {
    private final ActService actService;

    private final MessageSource messageSource;

    @ModelAttribute("act")
    public Act act(@PathVariable("actId") long actId) {
        return this.actService.findProduct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @ModelAttribute("status")
    public String status(@PathVariable("status") String actStatus) {
        return actStatus;
    }


    @PostMapping("turnStatus")
    public String turnStatusToCorrection(@ModelAttribute("act") Act act, @ModelAttribute("status") String actStatus) {

        switch (actStatus){
            case "correction" -> actService.updateActStatus(act.getId(), ActStatus.CORRECTION);
            case "checkingQC" -> actService.updateActStatus(act.getId(), ActStatus.CHECKING_QC);
            case "checkingPtd" -> actService.updateActStatus(act.getId(), ActStatus.CHECKING_PTD);
            case "checkingBD" -> actService.updateActStatus(act.getId(), ActStatus.CHECKING_BD);
            case "accepted" -> actService.updateActStatus(act.getId(), ActStatus.ACCEPTED);
            default -> throw new NoSuchElementException("catalogue.errors.act.not_found");
        }
        return "redirect:/catalogue/acts/list";
    }



    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0],
                        exception.getMessage(), locale));
        return "errors/404";
    }
}
