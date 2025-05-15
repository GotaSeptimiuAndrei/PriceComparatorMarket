// service/RecommendationService.java
package com.pricecomparatormarket.service;

import com.pricecomparatormarket.dto.response.RecommendationDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.RecommendationMapper;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.DiscountRow;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

  private final PriceSnapshotRepository snapshotRepo;
  private final DiscountRepository discountRepo;

  public List<RecommendationDto> bestBuys(String category, int limit) {

    LocalDate today =
        snapshotRepo
            .findTopByOrderByIdSnapshotDateDesc()
            .map(ps -> ps.getId().getSnapshotDate())
            .orElseThrow(() -> new NotFoundException("No snapshots available"));

    List<PriceSnapshot> snaps = snapshotRepo.findTodayByCategory(category, today);
    if (snaps.isEmpty()) {
      throw new NotFoundException("No snapshots available");
    }

    // any active discounts today (reuse repo projection)
    Map<Pair<Long, String>, DiscountRow> discByKey =
        discountRepo.findBestActiveDiscounts(today, Pageable.unpaged()).stream()
            .collect(
                Collectors.toMap(
                    row ->
                        Pair.of(
                            row.discount().getStore().getId(),
                            row.discount().getProduct().getProductId()),
                    row -> row));

    return snaps.stream()
        .map(
            ps -> {
              DiscountRow dr =
                  discByKey.get(Pair.of(ps.getStore().getId(), ps.getProduct().getProductId()));
              BigDecimal discounted = dr == null ? null : dr.discountedPrice();
              return RecommendationMapper.toDto(ps, discounted);
            })
        .sorted(Comparator.comparing(RecommendationDto::valuePerBaseUnit))
        .limit(limit)
        .toList();
  }
}
