package com.pricecomparatormarket.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateAlertRequest(
    @NotBlank String userEmail,
    @NotBlank String productId,
    Long storeId,
    @NotNull @Positive BigDecimal targetPrice) {}
