package com.pricecomparatormarket.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BestDiscountDto(
    String productId,
    String productName,
    String storeName,
    BigDecimal originalPrice,
    BigDecimal percentageOfDiscount,
    BigDecimal discountedPrice,
    String currency,
    LocalDate fromDate,
    LocalDate toDate) {}
