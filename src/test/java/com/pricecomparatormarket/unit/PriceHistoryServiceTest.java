package com.pricecomparatormarket.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pricecomparatormarket.dto.response.PriceHistoryDto;
import com.pricecomparatormarket.dto.response.PricePointDto;
import com.pricecomparatormarket.model.*;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import com.pricecomparatormarket.service.PriceHistoryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceHistoryServiceTest {

  private PriceSnapshotRepository repo;
  private PriceHistoryService service;

  @BeforeEach
  void init() {
    repo = mock(PriceSnapshotRepository.class);
    service = new PriceHistoryService(repo);
  }

  private PriceSnapshot ps(Store s, Product p, LocalDate d, BigDecimal price) {
    PriceSnapshot snap = mock(PriceSnapshot.class);

    PriceSnapshotId id = new PriceSnapshotId(s.getId(), p.getProductId(), d);
    when(snap.getId()).thenReturn(id);
    when(snap.getProduct()).thenReturn(p);
    when(snap.getStore()).thenReturn(s);
    when(snap.getPrice()).thenReturn(price);

    return snap;
  }

  @Test
  void groupsByProductAndStore_andSortsPoints() {
    Product prod = mock(Product.class);
    when(prod.getProductId()).thenReturn("P001");
    when(prod.getProductName()).thenReturn("Milk");

    Store lidl = mock(Store.class);
    when(lidl.getId()).thenReturn(1L);
    when(lidl.getName()).thenReturn("Lidl");

    Store kaufland = mock(Store.class);
    when(kaufland.getId()).thenReturn(2L);
    when(kaufland.getName()).thenReturn("Kaufland");

    List<PriceSnapshot> all =
        List.of(
            ps(lidl, prod, LocalDate.of(2025, 5, 1), new BigDecimal("10.00")),
            ps(lidl, prod, LocalDate.of(2025, 5, 8), new BigDecimal("9.80")),
            ps(kaufland, prod, LocalDate.of(2025, 5, 2), new BigDecimal("10.50")));

    when(repo.findHistoryFlat(null, null, null, null)).thenReturn(all);

    List<PriceHistoryDto> out = service.getHistory(null, null, null, null);

    assertEquals(2, out.size());

    PriceHistoryDto lidlSeries =
        out.stream().filter(dto -> "Lidl".equals(dto.storeName())).findFirst().orElseThrow();

    assertEquals(
        List.of(
            new PricePointDto(LocalDate.of(2025, 5, 1), new BigDecimal("10.00")),
            new PricePointDto(LocalDate.of(2025, 5, 8), new BigDecimal("9.80"))),
        lidlSeries.points());

    PriceHistoryDto kaufSeries =
        out.stream().filter(dto -> "Kaufland".equals(dto.storeName())).findFirst().orElseThrow();

    assertEquals(1, kaufSeries.points().size());
    assertEquals(LocalDate.of(2025, 5, 2), kaufSeries.points().getFirst().date());

    verify(repo).findHistoryFlat(null, null, null, null);
  }
}
