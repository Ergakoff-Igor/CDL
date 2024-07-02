package ru.docs.construction.manager.service;

import ru.docs.construction.manager.entity.Act;
import ru.docs.construction.manager.entity.ActStatus;

import java.util.List;
import java.util.Optional;

public interface ActService {

    List<Act> findAllProducts();

    Act createProduct(String month, String section, Double price);

    Optional<Act> findProduct(long productId);

    void updateProduct(Long id, String month, String section, Double price, ActStatus actStatus);

    void deleteProduct(Long id);

    void updateActStatus(Long id, ActStatus actStatus);
}
