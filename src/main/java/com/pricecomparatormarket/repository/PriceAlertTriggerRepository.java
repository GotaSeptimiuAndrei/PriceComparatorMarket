package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.PriceAlert;
import com.pricecomparatormarket.model.PriceAlertTrigger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceAlertTriggerRepository extends JpaRepository<PriceAlertTrigger, Long> {
  List<PriceAlertTrigger> findByAlert(PriceAlert alert);
}
