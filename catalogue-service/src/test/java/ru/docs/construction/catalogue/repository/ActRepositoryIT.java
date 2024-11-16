package ru.docs.construction.catalogue.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/sql/acts.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Интеграционные тесты ActRepository")
class ActRepositoryIT {

    @Autowired
    ActRepository actRepository;

    @Test
    @DisplayName("findAllBySectionLikeIgnoreCaseOrderById вернет список актов, отфильтрованный по разделу проекта с ранжироване=ием по id")
    void findAllBySectionLikeIgnoreCaseOrderById_ReturnsFilteredActsListOrderedById() {
        // given
        var filter = "%ЭМ%";

        // when
        var products = this.actRepository.findAllBySectionLikeIgnoreCaseOrderById(filter);

        // then
        assertEquals(List.of(new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD),
                new Act(3L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.ACCEPTED)), products);
    }

    @Test
    @DisplayName("findAllOrderById вернет полный список актов с ранжироване=ием по id")
    void findAllOrderById_ReturnsActsListOrderedById() {
        // given

        // when
        var products = this.actRepository.findAllOrderById();

        // then
        assertEquals(List.of(
                new Act(1L, "Февраль", (short) 2024, "ЭМ", 3000d, ActStatus.CHECKING_PTD),
                new Act(2L, "Февраль", (short) 2024, "АК", 1000d, ActStatus.CHECKING_QC),
                new Act(3L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.ACCEPTED),
                new Act(4L, "Март", (short) 2024, "ТХ", 4000d, ActStatus.CHECKING_QC)
        ), products);
    }
}