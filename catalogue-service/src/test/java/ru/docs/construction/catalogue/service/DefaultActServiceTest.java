package ru.docs.construction.catalogue.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;
import ru.docs.construction.catalogue.repository.ActRepository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты DefaultActService")
class DefaultActServiceTest {

    @Mock
    ActRepository actRepository;

    @InjectMocks
    DefaultActService service;

    @Test
    @DisplayName("findAllActs вернет полный список актов с ранжироване=ием по id")
    void findAllActs_FilterIsNotSet_ReturnsActsList() {
        // given
        var acts = LongStream.range(1, 4)
                .mapToObj(i -> new Act(i, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .toList();

        doReturn(acts).when(this.actRepository).findAllOrderById();

        // when
        var result = this.service.findAllActs(null);

        // then
        assertEquals(acts, result);

        verify(this.actRepository).findAllOrderById();
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("findAllActs вернет полный список актов с ранжироване=ием по id")
    void findAllActs_FilterIsBlank_ReturnsActsList() {
        // given
        var acts = LongStream.range(1, 4)
                .mapToObj(i -> new Act(i, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .toList();

        doReturn(acts).when(this.actRepository).findAllOrderById();

        // when
        var result = this.service.findAllActs(" ");

        // then
        assertEquals(acts, result);

        verify(this.actRepository).findAllOrderById();
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("findAllActs отфильтрует акты по разделу проекта и вернет список актов с ранжироване=ием по id")
    void findAllActs_FilterIsSet_ReturnsFilteredActsList() {
        // given
        var acts = LongStream.range(1, 4)
                .mapToObj(i -> new Act(i, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .toList();

        doReturn(acts).when(this.actRepository).findAllBySectionLikeIgnoreCaseOrderById("%ЭМ%");

        // when
        var result = this.service.findAllActs("ЭМ");

        // then
        assertEquals(acts, result);

        verify(this.actRepository).findAllBySectionLikeIgnoreCaseOrderById("%ЭМ%");
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("findAct вернет акт по id")
    void findAct_ActExists_ReturnsNotEmptyOptional() {
        // given
        var product = new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC);

        doReturn(Optional.of(product)).when(this.actRepository).findById(1L);

        // when
        var result = this.service.findAct(1L);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(product, result.orElseThrow());

        verify(this.actRepository).findById(1L);
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("findAct вернет пустой Optional")
    void findAct_ActDoesNotExist_ReturnsEmptyOptional() {
        // given

        // when
        var result = this.service.findAct(1L);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(this.actRepository).findById(1L);
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("createAct создаст акт, вернет новый акт")
    void createAct_ReturnsCreatedAct() {
        // given
        var month = "Январь";
        Short year = 2024;
        var section = "ЭМ";
        var price = 2000d;

        doReturn(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC))
                .when(this.actRepository).save(new Act(null, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC));

        // when
        var result = this.service.createAct(month, year, section, price);

        // then
        assertEquals(new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC), result);

        verify(this.actRepository).save(new Act(null, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC));
        verifyNoMoreInteractions(this.actRepository);
    }


    @Test
    @DisplayName("updateAct изменит акт")
    void updateAct_ActExists_UpdatesAct() {
        // given
        var actId = 1L;
        var act = new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC);
        var month = "Февраль";
        Short year = 2025;
        var section = "АК";
        var price = 3000d;
        var actStatus = ActStatus.CORRECTION;

        doReturn(Optional.of(act))
                .when(this.actRepository).findById(1L);

        // when
        this.service.updateAct(actId, month, year, section, price, actStatus);

        // then
        verify(this.actRepository).findById(actId);
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("updateAct выбросит NoSuchElementException")
    void updateAct_ActDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var actId = 1L;
        var month = "Февраль";
        Short year = 2025;
        var section = "АК";
        var price = 3000d;
        var actStatus = ActStatus.CORRECTION;

        // when
        assertThrows(NoSuchElementException.class, () -> this.service
                .updateAct(actId, month, year, section, price, actStatus));

        // then
        verify(this.actRepository).findById(actId);
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("deleteAct удалит акт")
    void deleteAct_DeletesAct() {
        // given
        var actId = 1L;

        // when
        this.service.deleteAct(actId);

        // then
        verify(this.actRepository).deleteById(actId);
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("updateActStatus изменит статус акта")
    void updateActStatus_ActExists_UpdatesActStatus() {
        // given
        var actId = 1L;
        var act = new Act(1L, "Январь", (short) 2024, "ЭМ", 2000d, ActStatus.CHECKING_QC);
        var actStatus = ActStatus.CORRECTION;

        doReturn(Optional.of(act))
                .when(this.actRepository).findById(1L);

        // when
        this.service.updateActStatus(actId, actStatus);

        // then
        verify(this.actRepository).findById(actId);
        verifyNoMoreInteractions(this.actRepository);
    }

    @Test
    @DisplayName("updateActStatus выбросит NoSuchElementException")
    void updateActStatus_ActDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var actId = 1L;
        var actStatus = ActStatus.CORRECTION;

        // when
        assertThrows(NoSuchElementException.class, () -> this.service
                .updateActStatus(actId, actStatus));

        // then
        verify(this.actRepository).findById(actId);
        verifyNoMoreInteractions(this.actRepository);
    }
}