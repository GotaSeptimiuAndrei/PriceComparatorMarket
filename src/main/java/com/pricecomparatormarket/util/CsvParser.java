package com.pricecomparatormarket.util;

import com.pricecomparatormarket.model.Unit;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;

/*
 * Utility class providing stream‑based parsing of the located CSV resources into
 * simple Java records (PriceRow, DiscountRow) using Apache Commons‑CSV. Each record
 * mirrors a line from the corresponding CSV and is forwarded to the ImportCsvService
 * for persistence.
 */
public final class CsvParser {

  private CsvParser() {}

  public static Stream<PriceRow> parsePriceCsv(Resource csv, String store, LocalDate snapshotDate)
      throws IOException {

    Reader rd = new InputStreamReader(csv.getInputStream(), StandardCharsets.UTF_8);
    Iterable<CSVRecord> recs = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(rd);

    return StreamSupport.stream(recs.spliterator(), false)
        .map(
            r -> {
              Unit unit = Unit.fromCsv(r.get("package_unit"));
              return new PriceRow(
                  r.get("product_id"),
                  r.get("product_name"),
                  r.get("product_category"),
                  r.get("brand"),
                  new BigDecimal(r.get("package_quantity")),
                  unit,
                  new BigDecimal(r.get("price")),
                  r.get("currency"),
                  store,
                  snapshotDate);
            });
  }

  public static Stream<DiscountRow> parseDiscountCsv(Resource csv, String store, LocalDate fileDate)
      throws IOException {

    Reader rd = new InputStreamReader(csv.getInputStream(), StandardCharsets.UTF_8);
    Iterable<CSVRecord> recs = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(rd);

    return StreamSupport.stream(recs.spliterator(), false)
        .map(
            r ->
                new DiscountRow(
                    r.get("product_id"),
                    new BigDecimal(r.get("percentage_of_discount")),
                    LocalDate.parse(r.get("from_date")),
                    LocalDate.parse(r.get("to_date")),
                    store,
                    fileDate));
  }

  public record PriceRow(
      String productId,
      String name,
      String category,
      String brand,
      BigDecimal quantity,
      Unit unit,
      BigDecimal price,
      String currency,
      String store,
      LocalDate date) {}

  public record DiscountRow(
      String productId,
      BigDecimal percentage,
      LocalDate fromDate,
      LocalDate toDate,
      String store,
      LocalDate fileDate) {}
}
