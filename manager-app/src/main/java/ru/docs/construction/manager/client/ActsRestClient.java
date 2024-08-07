package ru.docs.construction.manager.client;

import ru.docs.construction.manager.entity.Act;
import ru.docs.construction.manager.entity.ActStatus;

import java.util.List;
import java.util.Optional;

public interface ActsRestClient {

    List<Act> findAllActs();

    Act createAct(String month, Short year, String section, Double price);

    Optional<Act> findAct(long actId);

    void updateAct(Long id, String month, Short year, String section, Double price, ActStatus actStatus);

    void deleteAct(Long id);

    void updateActStatus(Long id, String actStatus);
}
