package ru.docs.construction.manager.controller.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record NewActPayload(
        @NotEmpty(message = "{catalogue.acts.create.errors.month_is_null}")
        String month,

        @NotNull(message = "{catalogue.acts.create.errors.year_is_null}")
        Short year,

        @NotEmpty(message = "{catalogue.acts.create.errors.section_is_null}")
        String section,

        Double price) {

}
