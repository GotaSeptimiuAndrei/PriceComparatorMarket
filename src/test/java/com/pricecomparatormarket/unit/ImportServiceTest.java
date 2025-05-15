 package com.pricecomparatormarket.unit;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.mockito.Mockito.*;

 import com.pricecomparatormarket.model.*;
 import com.pricecomparatormarket.repository.*;
 import com.pricecomparatormarket.service.AlertEvaluator;
 import com.pricecomparatormarket.service.ImportCsvService;
 import com.pricecomparatormarket.util.CsvFileLocator;
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.time.LocalDate;
 import java.util.List;
 import java.util.Optional;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.ArgumentCaptor;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.springframework.core.io.ByteArrayResource;

 @ExtendWith(MockitoExtension.class)
 class ImportServiceTest {

  CsvFileLocator locator;
  StoreRepository storeRepo;
  ProductRepository productRepo;
  PriceSnapshotRepository snapRepo;
  DiscountRepository discountRepo;
  ImportCsvService service;
  AlertEvaluator evaluator;

  @BeforeEach
  void init() {
    locator = mock(CsvFileLocator.class);
    storeRepo = mock(StoreRepository.class);
    productRepo = mock(ProductRepository.class);
    snapRepo = mock(PriceSnapshotRepository.class);
    discountRepo = mock(DiscountRepository.class);
    evaluator = mock(AlertEvaluator.class);

    service = new ImportCsvService(locator, storeRepo, productRepo, snapRepo, discountRepo, evaluator);
  }

  @Test
  void createsStoreAndUpsertsPriceSnapshot() throws IOException {
    var res =
        new org.springframework.core.io.ByteArrayResource(
            ("""

 product_id,product_name,product_category,brand,package_quantity,package_unit,price,currency
                P001,Milk,cat,Brand,1,l,5.00,RON
                """)
                .getBytes());
    CsvFileLocator.CsvMeta meta =
        new CsvFileLocator.CsvMeta(res, CsvFileLocator.CsvType.PRICE, "LIDL");
    when(locator.findForDate(LocalDate.of(2025, 5, 8))).thenReturn(List.of(meta));

    when(storeRepo.findByName("LIDL")).thenReturn(Optional.empty());
    when(storeRepo.save(any(Store.class)))
        .thenAnswer(
            inv -> {
              Store s = inv.getArgument(0);
              s.setId(1L);
              return s;
            });
    when(productRepo.findById("P001")).thenReturn(Optional.empty());
    when(productRepo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

    when(snapRepo.existsById(any(PriceSnapshotId.class))).thenReturn(false);

    service.importForDate(LocalDate.of(2025, 5, 8));

    verify(snapRepo).save(any(PriceSnapshot.class));
    verifyNoInteractions(discountRepo);
  }

  @Test
  void insertsDiscountRows() throws IOException {
    LocalDate date = LocalDate.of(2025, 5, 8);
    String csv =
        """
                product_id,percentage_of_discount,from_date,to_date
                P002,20.00,2025-05-08,2025-05-15
                """;
    var res = new ByteArrayResource(csv.getBytes());
    CsvFileLocator.CsvMeta discMeta =
        new CsvFileLocator.CsvMeta(res, CsvFileLocator.CsvType.DISCOUNT, "LIDL");
    when(locator.findForDate(date)).thenReturn(List.of(discMeta));

    Store store = new Store();
    store.setId(2L);
    store.setName("LIDL");
    when(storeRepo.findByName("LIDL")).thenReturn(Optional.of(store));

    Product product = new Product("P002", "iaurt grecesc", "lactate", "Lidl");
    when(productRepo.findById("P002")).thenReturn(Optional.of(product));

    service.importForDate(date);

    ArgumentCaptor<Discount> captor = ArgumentCaptor.forClass(Discount.class);
    verify(discountRepo).save(captor.capture());
    Discount saved = captor.getValue();

    assertEquals(store, saved.getStore());
    assertEquals(product, saved.getProduct());
    assertEquals(0, saved.getPercentageOfDiscount().compareTo(new BigDecimal("20.00")));
    assertEquals(LocalDate.of(2025, 5, 8), saved.getFromDate());
    assertEquals(LocalDate.of(2025, 5, 15), saved.getToDate());
  }
 }
