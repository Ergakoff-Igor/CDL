package ru.docs.construction.manager.controller;

import ru.docs.construction.manager.controller.payload.NewProductPayload;
import ru.docs.construction.manager.entity.Act;
import ru.docs.construction.manager.service.ActService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/acts")
public class ActsLogController {

    private final ActService actService;

    @GetMapping("list")
    public String getProductsList(Model model) {
        model.addAttribute("acts", this.actService.findAllProducts());
        return "catalogue/acts/index";
    }

    @GetMapping("create")
    public String getNewProductPage() {
        return "catalogue/acts/new_act";

    }

    @PostMapping("create")
    public String createProduct(@Valid NewProductPayload payload,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "catalogue/acts/new_act";
        } else {
            Act act = this.actService.createProduct(payload.month(), payload.section(), payload.price());
            return "redirect:/catalogue/acts/%d".formatted(act.getId());
        }
    }
}
