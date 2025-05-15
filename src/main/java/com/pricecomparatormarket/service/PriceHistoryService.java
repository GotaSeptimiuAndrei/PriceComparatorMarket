package com.pricecomparatormarket.service;

import com.pricecomparatormarket.dto.response.PriceHistoryDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.PriceHistoryMapper;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {

  private final PriceSnapshotRepository priceSnapshotRepo;

  public List<PriceHistoryDto> getHistory(
      String productId, Long storeId, String category, String brand) {

    List<PriceSnapshot> flat =
        priceSnapshotRepo.findHistoryFlat(productId, storeId, category, brand);

    if (flat.isEmpty()) {
      throw new NotFoundException("No price history found for productId: " + productId);
    }

    // group by product & store, preserving order
    record Key(String productId, Long storeId) {}
    Map<Key, List<PriceSnapshot>> grouped =
        flat.stream()
            .collect(
                Collectors.groupingBy(
                    ps -> new Key(ps.getProduct().getProductId(), ps.getStore().getId()),
                    LinkedHashMap::new,
                    Collectors.toList()));

    return grouped.values().stream()
        .map(
            list -> {
              PriceSnapshot first = list.get(0);
              return PriceHistoryMapper.toDto(first.getProduct(), first.getStore(), list);
            })
        .toList();
  }
}
