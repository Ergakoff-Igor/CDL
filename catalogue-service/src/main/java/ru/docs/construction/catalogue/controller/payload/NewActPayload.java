package ru.docs.construction.catalogue.controller.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record NewActPayload(
        @NotEmpty(message = "{catalogue.acts.create.errors.month_is_null}")
        String month,

        @NotNull(message = "{catalogue.acts.create.errors.year_is_null}")
        Short year,

        @NotEmpty(message = "{catalogue.acts.create.errors.section_is_null}")
        String section,

        Double price) {

}
