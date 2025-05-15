package com.pricecomparatormarket.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricePointDto(LocalDate date, BigDecimal price) {}
