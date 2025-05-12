package com.pricecomparatormarket.util;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/*
 * Component responsible for discovering price and discount CSV files in the application's
 * class‑path (src/main/resources/csv/). It applies naming‑convention regexes to determine
 * the store name, snapshot date and whether the file contains price or discount data.
 */
@Component
public class CsvFileLocator {

  private static final String CLASSPATH_PATTERN = "classpath:csv/*.csv";

  private static final Pattern PRICE_FILE =
      Pattern.compile("^(?<store>[a-z]+)_(?<date>\\d{4}-\\d{2}-\\d{2})\\.csv$");
  private static final Pattern DISC_FILE =
      Pattern.compile("^(?<store>[a-z]+)_discounts_(?<date>\\d{4}-\\d{2}-\\d{2})\\.csv$");

  public List<CsvMeta> findForDate(LocalDate date) throws IOException {
    Resource[] resources =
        new PathMatchingResourcePatternResolver().getResources(CLASSPATH_PATTERN);

    List<CsvMeta> out = new ArrayList<>();
    for (Resource res : resources) {
      String name = res.getFilename();
      if (name == null) continue;

      Matcher mPrice = PRICE_FILE.matcher(name);
      Matcher mDisc = DISC_FILE.matcher(name);

      if (mPrice.matches() && date.equals(LocalDate.parse(mPrice.group("date")))) {
        out.add(new CsvMeta(res, CsvType.PRICE, mPrice.group("store").toUpperCase()));
      } else if (mDisc.matches() && date.equals(LocalDate.parse(mDisc.group("date")))) {
        out.add(new CsvMeta(res, CsvType.DISCOUNT, mDisc.group("store").toUpperCase()));
      }
    }
    return out;
  }

  public record CsvMeta(Resource resource, CsvType type, String store) {}

  public enum CsvType {
    PRICE,
    DISCOUNT
  }
}
