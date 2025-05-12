package com.pricecomparatormarket.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.pricecomparatormarket.util.CsvFileLocator;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CsvFileLocatorTest {

  private final CsvFileLocator locator = new CsvFileLocator();

  @Test
  void findsPriceAndDiscountFilesForDate() throws IOException {
    LocalDate date = LocalDate.of(2025, 5, 8);

    List<CsvFileLocator.CsvMeta> found = locator.findForDate(date);

    assertEquals(6, found.size(), "Should pick up 6 price + 6 discount file");
    assertTrue(found.stream().anyMatch(m -> m.type() == CsvFileLocator.CsvType.PRICE));
    assertTrue(found.stream().anyMatch(m -> m.type() == CsvFileLocator.CsvType.DISCOUNT));
  }
}
