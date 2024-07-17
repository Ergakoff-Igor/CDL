package ru.docs.construction.manager.service;

import ru.docs.construction.manager.entity.Act;
import ru.docs.construction.manager.entity.ActStatus;
import ru.docs.construction.manager.repository.ActRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultActService implements ActService {

    private final ActRepository actRepository;

    @Override
    public List<Act> findAllActs() {
        return this.actRepository.findAll();
    }

    @Override
    public Act createAct(String month, Short year, String section, Double price) {
        return this.actRepository.save(new Act(null, month, year, section, price, ActStatus.CHECKING_QC));
    }

    @Override
    public Optional<Act> findAct(long actId) {
        return this.actRepository.findById(actId);
    }

    @Override
    public void updateAct(Long id, String month, Short year, String section, Double price, ActStatus actStatus) {
        this.actRepository.findById(id)
                .ifPresentOrElse(act -> {
                    act.setMonth(month);
                    act.setYear(year);
                    act.setSection(section);
                    act.setPrice(price);
                    act.setActStatus(actStatus);
                }, () -> {
                    throw new NoSuchElementException();
                });
    }

    @Override
    public void deleteAct(Long id) {
        this.actRepository.deleteById(id);
    }

    @Override
    public void updateActStatus(Long id, ActStatus actStatus) {
        this.actRepository.findById(id)
                .ifPresentOrElse(act -> act.setActStatus(actStatus), () -> {
                    throw new NoSuchElementException();
                });
    }
}
