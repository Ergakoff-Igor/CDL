package ru.docs.construction.catalogue.repository;

import ru.docs.construction.catalogue.entity.Act;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryActRepository implements ActRepository {

    private final List<Act> acts = Collections.synchronizedList(new LinkedList<>());

    @Override
    public List<Act> findAll() {
        return Collections.unmodifiableList(this.acts);
    }

    @Override
    public Act save(Act act) {
        act.setId(this.acts.stream()
                .max(Comparator.comparingLong(Act::getId))
                .map(Act::getId)
                .orElse(0L) + 1);
        this.acts.add(act);
        return act;
    }

    @Override
    public Optional<Act> findById(Long actId) {
        return this.acts.stream()
                .filter(act -> Objects.equals(actId, act.getId()))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        this.acts.removeIf(product -> Objects.equals(id, product.getId()));
    }
}
