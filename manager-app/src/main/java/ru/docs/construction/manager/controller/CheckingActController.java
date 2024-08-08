package ru.docs.construction.manager.controller;

import ru.docs.construction.manager.client.ActsRestClient;
import ru.docs.construction.manager.entity.Act;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping(("catalogue/acts/{actId:\\d+}/{status}"))
@RequiredArgsConstructor
public class CheckingActController {
    private final ActsRestClient actsRestClient;

    private final MessageSource messageSource;

    @ModelAttribute("act")
    public Act act(@PathVariable("actId") long actId) {
        return this.actsRestClient.findAct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @ModelAttribute("status")
    public String status(@PathVariable("status") String actStatus) {
        return actStatus;
    }

    @PostMapping("turnStatus")
    public String turnStatusToCorrection(@ModelAttribute("act") Act act, @ModelAttribute("status") String actStatus) {
        actsRestClient.updateActStatus(act.id(), actStatus);
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
