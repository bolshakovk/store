package com.store.store.services;

import com.store.store.models.enitities.Mp;
import com.store.store.repository.MpRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MpService {

    private final MpRepository mpRepository;
    private final StoreService storeService;

    public MpService(MpRepository mpRepository, StoreService storeService) {
        this.mpRepository = mpRepository;
        this.storeService = storeService;
    }

    public Mp create(Long storeId, Mp mp) {
        var store = storeService.getById(storeId);

        if (mpRepository.existsByNameAndStoreId(mp.getName(), storeId)) {
            throw new IllegalArgumentException("MP already exists in this store");
        }

        mp.setStore(store);
        return mpRepository.save(mp);
    }

    public List<Mp> getByStore(Long storeId) {
        return mpRepository.findByStoreId(storeId);
    }

    public Mp getById(Long id) {
        return mpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MP not found"));
    }

    public void delete(Long id) {
        mpRepository.deleteById(id);
    }
}