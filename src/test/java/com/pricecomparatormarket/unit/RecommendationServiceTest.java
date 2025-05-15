package com.pricecomparatormarket.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pricecomparatormarket.dto.response.RecommendationDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.model.*;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.DiscountRow;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import com.pricecomparatormarket.service.RecommendationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationServiceTest {

  private PriceSnapshotRepository snapshotRepo;
  private DiscountRepository discountRepo;
  private RecommendationService service;

  @BeforeEach
  void init() {
    snapshotRepo = mock(PriceSnapshotRepository.class);
    discountRepo = mock(DiscountRepository.class);
    service = new RecommendationService(snapshotRepo, discountRepo);
  }

  private PriceSnapshot snap(
      long storeId, String storeName, String productId, BigDecimal valuePerBase) {

    PriceSnapshot ps = mock(PriceSnapshot.class);

    LocalDate date = LocalDate.of(2025, 5, 15);
    when(ps.getId()).thenReturn(new PriceSnapshotId(storeId, productId, date));

    Store store = mock(Store.class);
    when(store.getId()).thenReturn(storeId);
    when(store.getName()).thenReturn(storeName);
    when(ps.getStore()).thenReturn(store);

    Product prod = mock(Product.class);
    when(prod.getProductId()).thenReturn(productId);
    when(prod.getProductName()).thenReturn("Name-" + productId);
    when(ps.getProduct()).thenReturn(prod);

    when(ps.getPackageQuantity()).thenReturn(BigDecimal.ONE);
    when(ps.getPackageUnit()).thenReturn(Unit.kg);
    when(ps.getPrice()).thenReturn(new BigDecimal("100"));
    when(ps.getCurrency()).thenReturn("RON");

    when(ps.getValuePerBaseUnit()).thenReturn(valuePerBase);

    return ps;
  }

  @Test
  void noSnapshotsAtAll_throwsNotFound() {
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(java.util.Optional.empty());

    assertThrows(NotFoundException.class, () -> service.bestBuys("any", 5));
  }

  @Test
  void noSnapshotsForCategory_throwsNotFound() {
    PriceSnapshot latest = snap(1, "S", "X", BigDecimal.ONE);

    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc())
        .thenReturn(java.util.Optional.of(latest));

    when(snapshotRepo.findTodayByCategory("cat", LocalDate.of(2025, 5, 15))).thenReturn(List.of());

    assertThrows(NotFoundException.class, () -> service.bestBuys("cat", 3));
  }

  @Test
  void snapshotsWithoutDiscounts_sortedByValue() {
    PriceSnapshot cheap = snap(1, "A", "P1", new BigDecimal("3.00"));
    PriceSnapshot pricey = snap(2, "B", "P2", new BigDecimal("5.00"));

    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc())
        .thenReturn(java.util.Optional.of(cheap));
    when(snapshotRepo.findTodayByCategory("cat", LocalDate.of(2025, 5, 15)))
        .thenReturn(List.of(cheap, pricey));
    when(discountRepo.findBestActiveDiscounts(LocalDate.of(2025, 5, 15), Pageable.unpaged()))
        .thenReturn(List.of());

    List<RecommendationDto> out = service.bestBuys("cat", 10);

    assertEquals(2, out.size());
    assertEquals(new BigDecimal("3.00"), out.get(0).valuePerBaseUnit());
    assertEquals(new BigDecimal("5.00"), out.get(1).valuePerBaseUnit());
  }

  @Test
  void appliesDiscounts_andRespectsLimit() {
    PriceSnapshot snap = snap(7L, "StoreC", "PX", new BigDecimal("4.00"));

    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(java.util.Optional.of(snap));
    when(snapshotRepo.findTodayByCategory("cat", LocalDate.of(2025, 5, 15)))
        .thenReturn(List.of(snap));

    com.pricecomparatormarket.model.Discount disc =
        mock(com.pricecomparatormarket.model.Discount.class);

    Store discStore = mock(Store.class);
    when(discStore.getId()).thenReturn(7L);
    when(disc.getStore()).thenReturn(discStore);

    Product discProd = mock(Product.class);
    when(discProd.getProductId()).thenReturn("PX");
    when(disc.getProduct()).thenReturn(discProd);

    DiscountRow row = new DiscountRow(disc, new BigDecimal("100"), new BigDecimal("80"), "RON");

    when(discountRepo.findBestActiveDiscounts(LocalDate.of(2025, 5, 15), Pageable.unpaged()))
        .thenReturn(List.of(row));

    List<RecommendationDto> result = service.bestBuys("cat", 1);

    assertEquals(1, result.size());
    RecommendationDto dto = result.getFirst();

    assertEquals(new BigDecimal("80"), dto.discountPrice());
    verify(snapshotRepo).findTodayByCategory("cat", LocalDate.of(2025, 5, 15));
    verify(discountRepo).findBestActiveDiscounts(LocalDate.of(2025, 5, 15), Pageable.unpaged());
  }
}
