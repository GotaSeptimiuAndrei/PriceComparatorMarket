package com.pricecomparatormarket.service;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.dto.response.NewDiscountDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.DiscountMapper;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountService {

  private final DiscountRepository discountRepo;
  private final PriceSnapshotRepository snapshotRepo;

  public List<BestDiscountDto> getBestDiscounts(int limit, LocalDate date) {
    LocalDate snapshotDate =
        (date != null)
            ? date
            : snapshotRepo
                .findTopByOrderByIdSnapshotDateDesc()
                .map(ps -> ps.getId().getSnapshotDate())
                .orElseThrow(() -> new NotFoundException("No snapshots available"));

    Pageable page = PageRequest.of(0, limit);

    return discountRepo.findBestActiveDiscounts(snapshotDate, page).stream()
        .map(DiscountMapper::toBestDto)
        .toList();
  }

  public List<NewDiscountDto> getNewDiscounts(
      @Min(1) @Max(168) int hours, @Min(1) @Max(20) int limit) {

    LocalDate snapshotDate =
        snapshotRepo
            .findTopByOrderByIdSnapshotDateDesc()
            .map(ps -> ps.getId().getSnapshotDate())
            .orElseThrow(() -> new NotFoundException("No snapshots available"));

    Instant cutoff = Instant.now().minus(hours, ChronoUnit.HOURS);
    Pageable page = PageRequest.of(0, limit);

    return discountRepo.findNewDiscounts(snapshotDate, cutoff, page).stream()
        .map(DiscountMapper::toNewDto)
        .toList();
  }
}
