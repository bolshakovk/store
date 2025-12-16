package com.store.store.repository;

import com.store.store.models.enitities.Mp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MpRepository extends JpaRepository<Mp, Long> {

    List<Mp> findByStoreId(Long storeId);

    boolean existsByNameAndStoreId(String name, Long storeId);
}
