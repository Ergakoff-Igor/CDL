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

    private final ActRepository productRepository;

    @Override
    public List<Act> findAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Act createProduct(String month, String section, Double price) {
        return this.productRepository.save(new Act(null, month, section, price, ActStatus.CHECKING_QC));
    }

    @Override
    public Optional<Act> findProduct(long actId) {
        return this.productRepository.findById(actId);
    }

    @Override
    public void updateProduct(Long id, String month, String section, Double price, ActStatus actStatus) {
        this.productRepository.findById(id)
                .ifPresentOrElse(act -> {
                    act.setMonth(month);
                    act.setSection(section);
                    act.setPrice(price);
                    act.setActStatus(actStatus);
                }, () -> {
                    throw new NoSuchElementException();
                });
    }

    @Override
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }

    @Override
    public void updateActStatus(Long id, ActStatus actStatus) {
        this.productRepository.findById(id)
                .ifPresentOrElse(act -> act.setActStatus(actStatus), () -> {
                    throw new NoSuchElementException();
                });
    }
}
