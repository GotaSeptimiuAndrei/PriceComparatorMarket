package com.pricecomparatormarket.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.dto.response.NewDiscountDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.DiscountMapper;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.model.PriceSnapshotId;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import com.pricecomparatormarket.service.DiscountService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;

class DiscountServiceTest {

  private DiscountRepository repo;
  private DiscountMapper mapper;
  private PriceSnapshotRepository snapshotRepo;
  private DiscountService service;

  @BeforeEach
  void init() {
    repo = mock(DiscountRepository.class);
    mapper = mock(DiscountMapper.class);
    snapshotRepo = mock(PriceSnapshotRepository.class);
    service = new DiscountService(repo, mapper, snapshotRepo);
  }

  @Test
  void givenExplicitDate_returnsMappedDtos() {
    LocalDate date = LocalDate.of(2025, 5, 8);
    int limit = 2;

    Object[] tuple =
        new Object[] {
          "ignored‑discount", BigDecimal.valueOf(11.50), BigDecimal.valueOf(9.20), "RON"
        };
    List<Object[]> tuples = List.<Object[]>of(tuple);
    BestDiscountDto dto =
        new BestDiscountDto(
            "P002",
            "Yogurt",
            "Lidl",
            BigDecimal.valueOf(11.50),
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(9.20),
            "RON",
            date,
            date.plusDays(7));

    when(repo.findBestActiveDiscounts(eq(date), any(Pageable.class))).thenReturn(tuples);
    when(mapper.fromTuple(tuple)).thenReturn(dto);

    List<BestDiscountDto> result = service.getBestDiscounts(limit, date);

    assertEquals(1, result.size());
    assertSame(dto, result.getFirst());

    ArgumentCaptor<Pageable> pCap = ArgumentCaptor.forClass(Pageable.class);
    verify(repo).findBestActiveDiscounts(eq(date), pCap.capture());
    assertEquals(limit, pCap.getValue().getPageSize());
    verifyNoInteractions(snapshotRepo);
  }

  @Test
  void whenDateNull_usesLatestSnapshotDate() {
    LocalDate latestDate = LocalDate.of(2025, 5, 9);
    PriceSnapshot mockPs = mock(PriceSnapshot.class);
    when(mockPs.getId()).thenReturn(new PriceSnapshotId(1L, "P1", latestDate));
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(Optional.of(mockPs));

    Object[] tuple = new Object[] {"d", BigDecimal.TEN, BigDecimal.ONE, "RON"};
    List<Object[]> tuples = List.<Object[]>of(tuple);
    BestDiscountDto dto = mock(BestDiscountDto.class);

    when(repo.findBestActiveDiscounts(eq(latestDate), any(Pageable.class))).thenReturn(tuples);
    when(mapper.fromTuple(tuple)).thenReturn(dto);

    List<BestDiscountDto> result = service.getBestDiscounts(5, null);

    assertEquals(1, result.size());
    verify(snapshotRepo).findTopByOrderByIdSnapshotDateDesc();
    verify(repo).findBestActiveDiscounts(eq(latestDate), any(Pageable.class));
  }

  @Test
  void whenNoSnapshots_throwNotFound() {
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getBestDiscounts(10, null));

    verifyNoInteractions(repo, mapper);
  }

  @Test
  void whenSnapshotExists_returnsMappedDtos() {
    LocalDate latestDate = LocalDate.of(2025, 5, 14);
    PriceSnapshot ps = mock(PriceSnapshot.class);
    when(ps.getId()).thenReturn(new PriceSnapshotId(1L, "PX", latestDate));
    when(snapshotRepo.findTopByOrderByIdSnapshotDateDesc()).thenReturn(Optional.of(ps));

    Object[] tuple = new Object[] {"d‑obj", BigDecimal.valueOf(10), BigDecimal.valueOf(8), "RON"};
    List<Object[]> tuples = List.<Object[]>of(tuple);

    when(repo.findNewDiscounts(eq(latestDate), any(Instant.class), any(Pageable.class)))
        .thenReturn(tuples);

    NewDiscountDto dto = mock(NewDiscountDto.class);
    when(mapper.fromNewTuple(tuple)).thenReturn(dto);

    List<NewDiscountDto> result = service.getNewDiscounts(24, 10);

    assertEquals(1, result.size());
    assertSame(dto, result.getFirst());

    ArgumentCaptor<Pageable> pCap = ArgumentCaptor.forClass(Pageable.class);
    verify(repo).findNewDiscounts(eq(latestDate), any(Instant.class), pCap.capture());
    assertEquals(10, pCap.getValue().getPageSize());
  }
}
