package ru.docs.construction.manager.controller;

import ru.docs.construction.manager.client.ActsRestClient;
import ru.docs.construction.manager.client.BadRequestException;
import ru.docs.construction.manager.controller.payload.NewActPayload;
import ru.docs.construction.manager.entity.Act;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/acts")
public class ActsLogController {

    private final ActsRestClient actsRestClient;

    @GetMapping("list")
    public String getActsList(Model model) {
        model.addAttribute("acts", this.actsRestClient.findAllActs());
        return "catalogue/acts/index";
    }

    @GetMapping("create")
    public String getNewActPage() {
        return "catalogue/acts/new_act";

    }

    @PostMapping("create")
    public String createAct(NewActPayload payload,
                                Model model) {
        try {
            Act act = this.actsRestClient.createAct(payload.month(), payload.year(), payload.section(), payload.price());
            return "redirect:/catalogue/acts/%d".formatted(act.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "catalogue/acts/new_act";
        }
    }
}
