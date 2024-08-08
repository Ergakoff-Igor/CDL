package ru.docs.construction.catalogue.service;

import ru.docs.construction.catalogue.entity.Act;
import ru.docs.construction.catalogue.entity.ActStatus;

import java.util.Optional;

public interface ActService {

    Iterable<Act> findAllActs(String filter);

    Act createAct(String month, Short year, String section, Double price);

    Optional<Act> findAct(Long productId);

    void updateAct(Long id, String month, Short year, String section, Double price, ActStatus actStatus);

    void deleteAct(Long id);

    void updateActStatus(Long id, ActStatus actStatus);

}
