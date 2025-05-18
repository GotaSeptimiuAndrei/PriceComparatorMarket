package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.Discount;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

  @Query(
      """
      SELECT new com.pricecomparatormarket.repository.DiscountRow(
                 d,
                 ps.price,
                 ps.price * (1 - d.percentageOfDiscount / 100),
                 ps.currency )
      FROM Discount d
      JOIN PriceSnapshot ps
        ON ps.store   = d.store
       AND ps.product = d.product
       AND ps.id.snapshotDate = :today
      WHERE :today BETWEEN d.fromDate AND d.toDate
      ORDER BY d.percentageOfDiscount DESC
      """)
  List<DiscountRow> findBestActiveDiscounts(@Param("today") LocalDate today, Pageable pageable);

  @Query(
      """
      SELECT new com.pricecomparatormarket.repository.DiscountRow(
                 d,
                 ps.price,
                 ps.price * (1 - d.percentageOfDiscount / 100),
                 ps.currency )
      FROM Discount d
      JOIN PriceSnapshot ps
        ON ps.store   = d.store
       AND ps.product = d.product
       AND ps.id.snapshotDate = :today
      WHERE d.createdAt >= :cutoff
        AND :today BETWEEN d.fromDate AND d.toDate
      ORDER BY d.createdAt DESC
      """)
  List<DiscountRow> findNewDiscounts(
      @Param("today") LocalDate today, @Param("cutoff") Instant cutoff, Pageable pageable);

  @Query(
      """
    SELECT d
    FROM   Discount d
    WHERE  d.product.productId = :productId
      AND  :today BETWEEN d.fromDate AND d.toDate
    """)
  List<Discount> findActiveForProduct(
      @Param("productId") String productId, @Param("today") LocalDate today);
}
