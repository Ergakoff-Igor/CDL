package ru.docs.construction.catalogue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Act {

    private Long id;

    private String month;

    private Short year;

    private String section;

    private Double price;

    private ActStatus actStatus;
}
