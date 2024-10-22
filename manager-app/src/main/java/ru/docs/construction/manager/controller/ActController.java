package ru.docs.construction.manager.controller;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ru.docs.construction.manager.client.ActsRestClient;
import ru.docs.construction.manager.client.BadRequestException;
import ru.docs.construction.manager.controller.payload.UpdateActPayload;
import ru.docs.construction.manager.entity.Act;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping(("catalogue/acts/{actId:\\d+}"))
@RequiredArgsConstructor
public class ActController {

    private final ActsRestClient actsRestClient;

    private final MessageSource messageSource;


    @ModelAttribute("act")
    public Act act(@PathVariable("actId") long actId) {
        return this.actsRestClient.findAct(actId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.act.not_found"));
    }

    @GetMapping
    public String getAct(@AuthenticationPrincipal OidcUser  oidcUser, Model model) {

        List<String> authorities = Optional.ofNullable(oidcUser.getClaimAsStringList("groups"))
                                        .orElseGet(List::of)
                                        .stream()
                                        .filter(role -> role.startsWith("ROLE_")).toList();
        LoggerFactory.getLogger(ActController.class).info("Principal {}", authorities);
        model.addAttribute("authorities", authorities);
        return "catalogue/acts/act";
    }

    @GetMapping("edit")
    public String getActEditPage() {
        return "catalogue/acts/edit";
    }

    @PostMapping("edit")
    public String updateAct(@ModelAttribute(name = "act") Act act,
                            UpdateActPayload payload,
                            Model model) {
        try {
            this.actsRestClient.updateAct(act.id(), payload.month(), payload.year(), payload.section(), payload.price(), act.actStatus());
            return "redirect:/catalogue/acts/%d".formatted(act.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "catalogue/acts/edit";
        }
    }

    @PostMapping("delete")
    public String deleteAct(@ModelAttribute("act") Act act) {
        this.actsRestClient.deleteAct(act.id());
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
