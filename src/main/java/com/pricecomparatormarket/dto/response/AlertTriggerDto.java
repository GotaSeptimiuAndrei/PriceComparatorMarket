package com.pricecomparatormarket.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlertTriggerDto(
    Long id, Long alertId, LocalDate snapshotDate, BigDecimal hitPrice, String storeName) {}
