package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
  Optional<Store> findByName(String name);
}
