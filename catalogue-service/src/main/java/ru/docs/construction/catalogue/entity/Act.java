package ru.docs.construction.catalogue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "acts_log", name = "t_act")
public class Act {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_month")
//    @NotNull
    private String month;

    @Column(name = "c_year")
//    @NotNull
    private Short year;

    @Column(name = "c_section")
//    @NotEmpty
    private String section;

    @Column(name = "c_price")
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_act_status")
    private ActStatus actStatus;
}
