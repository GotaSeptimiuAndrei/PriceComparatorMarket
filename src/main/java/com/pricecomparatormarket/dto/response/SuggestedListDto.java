package com.pricecomparatormarket.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SuggestedListDto(String storeName, BigDecimal subTotal, List<Item> items) {

  public record Item(
      String productId,
      String productName,
      BigDecimal quantity,
      BigDecimal unitPrice,
      BigDecimal linePrice) {}
}
