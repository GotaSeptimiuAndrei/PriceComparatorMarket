package com.pricecomparatormarket.mapper;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.model.Discount;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Converts (Discount + runtime price numbers) -> DTO.
@Mapper(componentModel = "spring")
public interface DiscountMapper {

  @Mapping(target = "productId", source = "discount.product.productId")
  @Mapping(target = "productName", source = "discount.product.productName")
  @Mapping(target = "storeName", source = "discount.store.name")
  @Mapping(target = "percentageOfDiscount", source = "discount.percentageOfDiscount")
  @Mapping(target = "currency", expression = "java(discount.getProduct().getCurrency())")
  @Mapping(target = "fromDate", source = "discount.fromDate")
  @Mapping(target = "toDate", source = "discount.toDate")
  BestDiscountDto toDto(Discount discount, BigDecimal originalPrice, BigDecimal discountedPrice);

  default BestDiscountDto fromTuple(Object[] tuple) {
    Discount d = (Discount) tuple[0];
    BigDecimal original = (BigDecimal) tuple[1];
    BigDecimal after = (BigDecimal) tuple[2];
    return toDto(d, original, after);
  }
}
