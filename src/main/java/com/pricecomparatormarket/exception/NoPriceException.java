package com.pricecomparatormarket.exception;

// throw if a product has no price snapshot today
public class NoPriceException extends RuntimeException {
  public NoPriceException(String message) {
    super(message);
  }
}
