package com.store.store.services;

import com.store.store.models.enitities.Store;
import com.store.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store create(Store store) {
        if (storeRepository.existsByName(store.getName())) {
            throw new IllegalArgumentException("Store with this name already exists");
        }
        return storeRepository.save(store);
    }

    public List<Store> getAll() {
        return storeRepository.findAll();
    }

    public Store getById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    public void delete(Long id) {
        storeRepository.deleteById(id);
    }
}