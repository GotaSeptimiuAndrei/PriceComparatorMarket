package com.pricecomparatormarket.service;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.DiscountMapper;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountService {

  private final DiscountRepository repo;
  private final DiscountMapper mapper;
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
    return repo.findBestActiveDiscounts(snapshotDate, page).stream()
        .map(mapper::fromTuple)
        .toList();
  }
}
