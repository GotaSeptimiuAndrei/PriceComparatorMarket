package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.PriceAlert;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

  /* all ACTIVE alerts for a product (optionally store) whose target ≥ currentPrice */
  @Query(
      """
      SELECT a
      FROM   PriceAlert a
      WHERE  a.active = TRUE
        AND  a.product.productId = :productId
        AND  (:storeId IS NULL OR a.store.id = :storeId)
        AND  a.targetPrice >= :currentPrice
      """)
  List<PriceAlert> findMatching(
      @Param("productId") String productId,
      @Param("storeId") Long storeId,
      @Param("currentPrice") BigDecimal currentPrice);

  List<PriceAlert> findByUserEmail(String userEmail);
}
