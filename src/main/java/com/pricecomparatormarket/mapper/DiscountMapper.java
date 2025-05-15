package com.pricecomparatormarket.mapper;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.dto.response.NewDiscountDto;
import com.pricecomparatormarket.model.Discount;
import com.pricecomparatormarket.repository.DiscountRow;

public final class DiscountMapper {
  private DiscountMapper() {}

  public static BestDiscountDto toBestDto(DiscountRow row) {
    Discount d = row.discount();
    return new BestDiscountDto(
        d.getProduct().getProductId(),
        d.getProduct().getProductName(),
        d.getStore().getName(),
        row.originalPrice(),
        d.getPercentageOfDiscount(),
        row.discountedPrice(),
        row.currency(),
        d.getFromDate(),
        d.getToDate());
  }

  public static NewDiscountDto toNewDto(DiscountRow row) {
    Discount d = row.discount();
    return new NewDiscountDto(
        d.getProduct().getProductId(),
        d.getProduct().getProductName(),
        d.getStore().getName(),
        row.originalPrice(),
        d.getPercentageOfDiscount(),
        row.discountedPrice(),
        row.currency(),
        d.getFromDate(),
        d.getToDate(),
        d.getCreatedAt());
  }
}
