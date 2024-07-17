package ru.docs.construction.manager.controller;

import org.springframework.validation.annotation.Validated;
import ru.docs.construction.manager.controller.payload.UpdateActPayload;
import ru.docs.construction.manager.entity.Act;
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


@Controller
@RequestMapping(("catalogue/acts/{actId:\\d+}"))
@RequiredArgsConstructor
public class ActController {

    private final ActService actService;

    private final MessageSource messageSource;

    @ModelAttribute("act")
    public Act act(@PathVariable("actId") long actId) {
        return this.actService.findAct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @GetMapping
    public String getAct() {
        return "catalogue/acts/act";
    }

    @GetMapping("edit")
    public String getActEditPage() {
        return "catalogue/acts/edit";
    }

    @PostMapping("edit")
    public String updateAct(@ModelAttribute(name = "act", binding = false) Act act,
                                @Validated UpdateActPayload payload,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "catalogue/acts/edit";
        } else {
            this.actService.updateAct(act.getId(), payload.month(), payload.section(), payload.price(), act.getActStatus());
            return "redirect:/catalogue/acts/%d".formatted(act.getId());
        }
    }

    @PostMapping("delete")
    public String deleteAct(@ModelAttribute("act") Act act) {
        this.actService.deleteAct(act.getId());
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
