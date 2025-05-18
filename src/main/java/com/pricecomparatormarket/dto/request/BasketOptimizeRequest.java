package com.pricecomparatormarket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record BasketOptimizeRequest(
    @NotBlank String name,
    @NotBlank String userEmail,
    @Positive int maxStores,
    @Size(min = 1) List<Item> items) {

  public record Item(@NotBlank String productId, @NotNull BigDecimal quantity) {}
}
