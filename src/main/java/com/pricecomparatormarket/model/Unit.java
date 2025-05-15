package com.pricecomparatormarket.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Unit {
  kg("kg", BigDecimal.ONE), // base for weight
  g("g", new BigDecimal("0.001")),
  l("l", BigDecimal.ONE), // base for volume
  ml("ml", new BigDecimal("0.001")),
  buc("buc", BigDecimal.ONE), // pieces
  role("role", BigDecimal.ONE); // toilet-paper rolls

  private final String display;
  private final BigDecimal toBaseFactor;

  Unit(String display, BigDecimal toBaseFactor) {
    this.display = display;
    this.toBaseFactor = toBaseFactor;
  }

  /** Converts any quantity to its base (kg, l, piece). */
  public BigDecimal toBase(BigDecimal qty) {
    return qty.multiply(toBaseFactor);
  }

  public String display() {
    return display;
  }

  /** Convenience: parses CSV strings like “kg”, “buc”, case-insensitive. */
  public static Unit fromCsv(String raw) {
    return valueOf(raw.trim().toLowerCase());
  }

  /** Price ÷ base-qty → ron/kg or ron/l etc. */
  public BigDecimal valuePerBase(BigDecimal price, BigDecimal qty) {
    return price.divide(toBase(qty), 4, RoundingMode.HALF_UP);
  }
}
