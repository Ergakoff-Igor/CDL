package ru.docs.construction.manager.controller.payload;

import ru.docs.construction.manager.entity.ActStatus;

public record UpdateActPayload(String month, Short year, String section, Double price, ActStatus actStatus) {
}
