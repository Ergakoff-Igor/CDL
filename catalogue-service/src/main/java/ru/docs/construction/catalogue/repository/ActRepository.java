package ru.docs.construction.catalogue.repository;

import org.springframework.data.repository.CrudRepository;
import ru.docs.construction.catalogue.entity.Act;

public interface ActRepository extends CrudRepository<Act, Long> {

    Iterable<Act> findAllBySectionLikeIgnoreCase(String name);
}
