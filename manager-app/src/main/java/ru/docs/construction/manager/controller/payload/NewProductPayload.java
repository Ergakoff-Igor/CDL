package ru.docs.construction.manager.controller.payload;

import jakarta.validation.constraints.NotNull;

public record NewProductPayload(
        @NotNull(message = "{catalogue.acts.create.errors.month_is_null}")
        String month,

        @NotNull(message = "{catalogue.acts.create.errors.section_is_null}")
        String section,

        Double price) {

}
