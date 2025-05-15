package com.pricecomparatormarket.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.dto.response.NewDiscountDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.DiscountMapper;
import com.pricecomparatormarket.model.*;
import com.pricecomparatormarket.repository.*;
import com.pricecomparatormarket.service.DiscountService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class DiscountServiceTest {

  private DiscountRepository repo;
  private PriceSnapshotRepository snapshotRepo;
  private DiscountService service;

  @BeforeEach
  void init() {
    repo = mock(DiscountRepository.class);
    snapshotRepo = mock(PriceSnapshotRepository.class);
    service = new DiscountService(repo, snapshotRepo);
  }

  private DiscountRow bestRow(LocalDate from, LocalDate to, BigDecimal pct) {
    Product product = mock(Product.class);
    when(product.getProductId()).thenReturn("P002");
    when(product.getProductName()).thenReturn("Yogurt");

    Store store = mock(Store.class);
    when(store.getName()).thenReturn("Lidl");

    Discount d = mock(Discount.class);
    when(d.getProduct()).thenReturn(product);
    when(d.getStore()).thenReturn(store);
    when(d.getPercentageOfDiscount()).thenReturn(pct);
    when(d.getFromDate()).thenReturn(from);
    when(d.getToDate()).thenReturn(to);

    return new DiscountRow(d, new BigDecimal("11.50"), new BigDecimal("9.20"), "RON");
  }

  private DiscountRow newRow(LocalDate from, LocalDate to, BigDecimal pct, Instant created) {
    DiscountRow base = bestRow(from, to, pct);
    Discount d = base.discount();
    lenient().when(d.getCreatedAt()).thenReturn(created);
    return base;
  }

  @Test
  void givenExplicitDate_returnsMappedDtos() {
    LocalDate date = LocalDate.of(2025, 5, 8);
    int limit = 2;

    DiscountRow row = bestRow(date, date.plusDays(7), new BigDecimal("20"));
    when(repo.findBestActiveDiscounts(eq(date), any(Pageable.class))).thenReturn(List.of(row));

    List<BestDiscountDto> result = service.getBestDiscounts(limit, date);

    assertEquals(List.of(DiscountMapper.toBestDto(row)), result);

    ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
    verify(repo).findBestActiveDiscounts(eq(date), cap.capture());
    assertEquals(limit, cap.getValue().getPageSize());
    verifyNoInteractions(snapshotRepo);
  }

  @Test
  void whenDateNull_usesLatestSnapshotDate() {
    LocalDate latest = LocalDate.of(2025, 5, 9);

    PriceSnapshot ps = mock(PriceSnapshot.class);
    when(ps.getId()).thenReturn(new PriceSnapshotId(1L, "PX", latest));
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(Optional.of(ps));

    DiscountRow row = bestRow(latest, latest.plusDays(5), new BigDecimal("15"));
    when(repo.findBestActiveDiscounts(eq(latest), any(Pageable.class))).thenReturn(List.of(row));

    List<BestDiscountDto> out = service.getBestDiscounts(3, null);

    assertEquals(1, out.size());
    verify(snapshotRepo).findTopByOrderByIdSnapshotDateDesc();
  }

  @Test
  void whenNoSnapshots_throwNotFound() {
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getBestDiscounts(5, null));

    verifyNoInteractions(repo);
  }

  @Test
  void newDiscounts_returnsDtos() {
    LocalDate latest = LocalDate.of(2025, 5, 14);
    PriceSnapshot ps = mock(PriceSnapshot.class);
    when(ps.getId()).thenReturn(new PriceSnapshotId(2L, "P1", latest));
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(Optional.of(ps));

    Instant created = Instant.parse("2025-05-13T12:00:00Z");
    DiscountRow row =
        newRow(latest.minusDays(1), latest.plusDays(6), new BigDecimal("10"), created);

    when(repo.findNewDiscounts(eq(latest), any(Instant.class), any(Pageable.class)))
        .thenReturn(List.of(row));

    List<NewDiscountDto> out = service.getNewDiscounts(24, 7);

    assertEquals(List.of(DiscountMapper.toNewDto(row)), out);
  }
}
