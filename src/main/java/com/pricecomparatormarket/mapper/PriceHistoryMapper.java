package com.pricecomparatormarket.mapper;

import com.pricecomparatormarket.dto.response.PriceHistoryDto;
import com.pricecomparatormarket.dto.response.PricePointDto;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.Product;
import com.pricecomparatormarket.model.Store;
import java.util.Comparator;
import java.util.List;

public final class PriceHistoryMapper {
  private PriceHistoryMapper() {}

  public static PriceHistoryDto toDto(Product product, Store store, List<PriceSnapshot> snapshots) {

    List<PricePointDto> points =
        snapshots.stream()
            .sorted(Comparator.comparing(ps -> ps.getId().getSnapshotDate()))
            .map(ps -> new PricePointDto(ps.getId().getSnapshotDate(), ps.getPrice()))
            .toList();

    return new PriceHistoryDto(
        product.getProductId(),
        product.getProductName(),
        store != null ? store.getName() : null,
        points);
  }
}
