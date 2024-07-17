package ru.docs.construction.manager.controller.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record NewActPayload(
        @NotEmpty(message = "{catalogue.acts.create.errors.month_is_null}")
//        @NotEmpty(message = "{catalogue.products.create.errors.title_is_null}")
//        @Size(min = 3, max = 50, message = "{catalogue.products.create.errors.title_size_is_invalid}")
        String month,

        Short year,

        @NotEmpty(message = "{catalogue.acts.create.errors.section_is_null}")
//        @Size(max = 1000, message = "{catalogue.products.create.errors.details_size_is_invalid}")
        String section,

        Double price) {

}
