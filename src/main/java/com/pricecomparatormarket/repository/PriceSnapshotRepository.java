package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.PriceSnapshotId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, PriceSnapshotId> {

  @Query(
      """
        SELECT  ps
        FROM    PriceSnapshot ps
        JOIN FETCH ps.product p
        JOIN FETCH ps.store   s
        WHERE  (:productId IS NULL OR p.productId       = :productId)
          AND  (:storeId   IS NULL OR s.id              = :storeId)
          AND  (:category  IS NULL OR p.productCategory = :category)
          AND  (:brand     IS NULL OR p.brand           = :brand)
        """)
  List<PriceSnapshot> findHistoryFlat(
      @Param("productId") String productId,
      @Param("storeId") Long storeId,
      @Param("category") String category,
      @Param("brand") String brand);

  Optional<PriceSnapshot> findTopByOrderByIdSnapshotDateDesc();

  @Query(
      """
      SELECT ps
      FROM   PriceSnapshot ps
      JOIN FETCH ps.product p
      JOIN FETCH ps.store   s
      WHERE  p.productCategory = :category
        AND  ps.id.snapshotDate = :today
      """)
  List<PriceSnapshot> findTodayByCategory(
      @Param("category") String category, @Param("today") LocalDate today);

  @Query(
      """
        SELECT ps
        FROM   PriceSnapshot ps
        WHERE  ps.store.id = :storeId
          AND  ps.product.productId = :productId
          AND  ps.id.snapshotDate = :date
        """)
  Optional<PriceSnapshot> findOneByStoreProductAndDate(
      Long storeId, String productId, LocalDate date);

  @Query(
      """
    SELECT ps
    FROM   PriceSnapshot ps
    WHERE  ps.product.productId = :productId
      AND  ps.id.snapshotDate   = :today
    """)
  List<PriceSnapshot> findTodayByProduct(
      @Param("productId") String productId, @Param("today") LocalDate today);
}
