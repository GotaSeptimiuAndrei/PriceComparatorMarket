package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.PriceSnapshotId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, PriceSnapshotId> {}
