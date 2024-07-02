package ru.docs.construction.manager.controller.payload;

import jakarta.validation.constraints.NotNull;

public record UpdateProductPayload(
        @NotNull(message = "{catalogue.acts.update.errors.month_is_null}")
        String month,

        @NotNull(message = "{catalogue.acts.update.errors.section_is_null}")
        String section,

        Double price) {
}
