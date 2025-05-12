package com.pricecomparatormarket.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.pricecomparatormarket.util.CsvParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

class CsvParserTest {

  @Test
  void parsesPriceCsvLineIntoRecord() throws IOException {
    String csv =
        """
                product_id,product_name,product_category,brand,package_quantity,package_unit,price,currency
                P999,test prod,cat,BrandX,2,kg,10.50,RON
                """;
    var res = new ByteArrayResource(csv.getBytes());
    var rows = CsvParser.parsePriceCsv(res, "LIDL", LocalDate.of(2025, 5, 8)).toList();

    assertEquals(1, rows.size());
    var row = rows.get(0);
    assertEquals("P999", row.productId());
    assertEquals(new BigDecimal("10.50"), row.price());
    assertEquals("kg", row.unit());
  }

  @Test
  void parsesDiscountCsvLineIntoRecord() throws IOException {
    String csv =
        """
                product_id,percentage_of_discount,from_date,to_date
                P999,15.00,2025-05-08,2025-05-14
                """;
    var res = new ByteArrayResource(csv.getBytes());
    var rows = CsvParser.parseDiscountCsv(res, "LIDL", LocalDate.of(2025, 5, 8)).toList();

    assertEquals(1, rows.size());
    var row = rows.get(0);
    assertEquals("P999", row.productId());
    assertEquals(new BigDecimal("15.00"), row.percentage());
    assertEquals(LocalDate.of(2025, 5, 14), row.toDate());
  }
}
