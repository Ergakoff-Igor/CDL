package ru.docs.construction.manager.controller.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateActPayload(
        @NotEmpty(message = "{catalogue.acts.update.errors.month_is_null}")
        String month,

        @NotEmpty(message = "{catalogue.acts.update.errors.section_is_null}")
        String section,

        Double price) {
}
