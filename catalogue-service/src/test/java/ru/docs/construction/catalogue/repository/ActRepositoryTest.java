package ru.docs.construction.catalogue.repository;

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
class ActRepositoryTest {

    @Autowired
    ActRepository actRepository;
    @Test
    void findAllBySectionLikeIgnoreCaseOrderById_ReturnsFilteredActsList() {
        // given
        var filter = "%ЭМ%";

        // when
        var products = this.actRepository.findAllBySectionLikeIgnoreCaseOrderById(filter);

        // then
        assertEquals(List.of(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC)), products);
    }

    @Test
    void findAllOrderById() {
    }
}