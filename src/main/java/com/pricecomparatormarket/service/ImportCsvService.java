package com.pricecomparatormarket.service;

import com.pricecomparatormarket.model.*;
import com.pricecomparatormarket.repository.DiscountRepository;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import com.pricecomparatormarket.repository.ProductRepository;
import com.pricecomparatormarket.repository.StoreRepository;
import com.pricecomparatormarket.util.CsvFileLocator;
import com.pricecomparatormarket.util.CsvParser;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ImportCsvService {

  private final CsvFileLocator csvFileLocator;
  private final StoreRepository storeRepository;
  private final ProductRepository productRepository;
  private final PriceSnapshotRepository priceSnapshotRepository;
  private final DiscountRepository discountRepository;
  private final AlertEvaluator alertEvaluator;

  /** Imports all price and discount CSVs for the given date into the database. */
  public void importForDate(LocalDate date) throws IOException {

    for (var meta : csvFileLocator.findForDate(date)) {

      Store store =
          storeRepository
              .findByName(meta.store())
              .orElseGet(() -> storeRepository.save(new Store(null, meta.store())));

      switch (meta.type()) {
        case PRICE ->
            CsvParser.parsePriceCsv(meta.resource(), store.getName(), date)
                .forEach(r -> upsertPrice(store, r));

        case DISCOUNT ->
            CsvParser.parseDiscountCsv(meta.resource(), store.getName(), date)
                .forEach(r -> insertDiscount(store, r, date)); // <── pass date
      }
    }
  }

  private void upsertPrice(Store store, CsvParser.PriceRow r) {
    Product product =
        productRepository
            .findById(r.productId())
            .orElseGet(
                () ->
                    productRepository.save(
                        new Product(r.productId(), r.name(), r.category(), r.brand())));
    PriceSnapshotId id = new PriceSnapshotId(store.getId(), product.getProductId(), r.date());
    if (priceSnapshotRepository.existsById(id)) {
      return;
    }
    PriceSnapshot snap =
        priceSnapshotRepository.save(
            new PriceSnapshot(id, store, product, r.quantity(), r.unit(), r.price(), r.currency()));
    alertEvaluator.evaluate(snap);
  }

  private void insertDiscount(Store store, CsvParser.DiscountRow r, LocalDate fileDate) {

    productRepository
        .findById(r.productId())
        .ifPresent(
            prod -> {
              Discount d = new Discount();
              d.setStore(store);
              d.setProduct(prod);
              d.setFromDate(r.fromDate());
              d.setToDate(r.toDate());
              d.setPercentageOfDiscount(r.percentage());

              discountRepository.save(d);

              /* evaluate only if fileDate is within the discount interval */
              alertEvaluator.evaluate(d, fileDate);
            });
  }
}
