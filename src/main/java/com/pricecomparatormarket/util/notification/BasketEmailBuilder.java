package com.pricecomparatormarket.util.notification;

import com.pricecomparatormarket.dto.response.SuggestedListDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BasketEmailBuilder {

  public String buildEmail(String basketName, List<SuggestedListDto> lists, BigDecimal grandTotal) {

    StringBuilder sb = new StringBuilder();
    sb.append("🛒 Cheapest split for basket '").append(basketName).append("'\n\n");

    for (SuggestedListDto l : lists) {
      sb.append("Store: ")
          .append(l.storeName())
          .append("  |  subtotal: ")
          .append(l.subTotal())
          .append(" RON\n");
      l.items()
          .forEach(
              i ->
                  sb.append("  - ")
                      .append(i.productName())
                      .append(" × ")
                      .append(i.quantity())
                      .append("  @ ")
                      .append(i.unitPrice())
                      .append("\n"));
      sb.append('\n');
    }
    sb.append("TOTAL: ").append(grandTotal).append(" RON");
    return sb.toString();
  }
}
