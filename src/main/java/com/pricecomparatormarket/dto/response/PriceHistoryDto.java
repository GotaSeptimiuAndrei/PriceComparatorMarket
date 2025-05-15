package com.pricecomparatormarket.dto.response;

import java.util.List;

public record PriceHistoryDto(
    String productId, String productName, String storeName, List<PricePointDto> points) {}
