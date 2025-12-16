package com.store.store.repository;

import com.store.store.models.enitities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByName(String name);
}