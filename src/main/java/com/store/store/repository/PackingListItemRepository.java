package com.store.store.repository;

import com.store.store.models.enitities.PackingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackingListItemRepository extends JpaRepository<PackingList, Long> {
    List<PackingList> findByMpId(Long mpId);
    @Query("""
       select p from PackingList p
       where p.markNumber like %:q%
          or p.drawingNumber like %:q%
       """)
    List<PackingList> searchByMarkOrDrawing(@Param("q") String query);
}