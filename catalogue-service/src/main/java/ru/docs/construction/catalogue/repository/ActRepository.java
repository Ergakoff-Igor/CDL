package ru.docs.construction.catalogue.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.docs.construction.catalogue.entity.Act;

public interface ActRepository extends CrudRepository<Act, Long> {

    @Query(value = "select * FROM acts_log.t_act where c_section ilike :filter order by id", nativeQuery = true)
    Iterable<Act> findAllBySectionLikeIgnoreCaseOrderById(@Param("filter") String name);

    @Query(value = "select * FROM acts_log.t_act order by id", nativeQuery = true)
    Iterable<Act> findAllOrderById();
}
