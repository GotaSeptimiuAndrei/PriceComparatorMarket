package com.pricecomparatormarket.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecommendationDto(
    String productId,
    String productName,
    String brand,
    BigDecimal packageQuantity,
    String unit, // "kg", "l", "buc", "role"
    BigDecimal shelfPrice, // today’s tag price
    BigDecimal discountPrice, // nullable – only if a discount exists
    BigDecimal valuePerBaseUnit, // RON/kg, RON/l, RON/buc …
    String currency,
    String storeName,
    LocalDate snapshotDate) {}
