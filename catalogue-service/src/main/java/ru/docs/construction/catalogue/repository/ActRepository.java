package ru.docs.construction.catalogue.repository;

import ru.docs.construction.catalogue.entity.Act;

import java.util.List;
import java.util.Optional;

public interface ActRepository {

    List<Act> findAll();

    Act save(Act act);

    Optional<Act> findById(Long atId);

    void deleteById(Long id);
}
