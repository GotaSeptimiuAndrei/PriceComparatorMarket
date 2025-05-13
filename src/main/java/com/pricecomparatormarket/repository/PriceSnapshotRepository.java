package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.PriceSnapshotId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, PriceSnapshotId> {
  Optional<PriceSnapshot> findTopByOrderByIdSnapshotDateDesc();
}
