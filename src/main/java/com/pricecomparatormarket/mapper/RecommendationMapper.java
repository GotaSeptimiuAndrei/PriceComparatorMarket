// service/RecommendationMapper.java  (all-static like your other mappers)
package com.pricecomparatormarket.mapper;

import com.pricecomparatormarket.dto.response.RecommendationDto;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.Product;
import java.math.BigDecimal;

public final class RecommendationMapper {
  private RecommendationMapper() {}

  public static RecommendationDto toDto(PriceSnapshot ps, BigDecimal discountPrice) {

    Product p = ps.getProduct();
    return new RecommendationDto(
        p.getProductId(),
        p.getProductName(),
        p.getBrand(),
        ps.getPackageQuantity(),
        ps.getPackageUnit().display(), // "kg", "buc"…
        ps.getPrice(),
        discountPrice,
        ps.getValuePerBaseUnit(),
        ps.getCurrency(),
        ps.getStore().getName(),
        ps.getId().getSnapshotDate());
  }
}
