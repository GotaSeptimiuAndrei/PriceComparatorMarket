package com.pricecomparatormarket.dto.response;

import java.math.BigDecimal;

public record AlertDto(
    Long id,
    String userEmail,
    String productId,
    String productName,
    Long storeId,
    String storeName,
    BigDecimal targetPrice,
    boolean active) {}
