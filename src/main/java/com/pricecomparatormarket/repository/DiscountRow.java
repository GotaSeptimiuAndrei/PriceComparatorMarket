package com.pricecomparatormarket.repository;

import com.pricecomparatormarket.model.Discount;
import java.math.BigDecimal;

/**
 * Read-side projection used by DiscountRepository. One row = one discount + its price information
 * for a given snapshot date.
 */
public record DiscountRow(
    Discount discount, BigDecimal originalPrice, BigDecimal discountedPrice, String currency) {}
