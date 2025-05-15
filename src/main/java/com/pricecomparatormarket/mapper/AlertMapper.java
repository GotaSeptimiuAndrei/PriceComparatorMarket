package com.pricecomparatormarket.mapper;

import com.pricecomparatormarket.dto.response.AlertDto;
import com.pricecomparatormarket.dto.response.AlertTriggerDto;
import com.pricecomparatormarket.model.PriceAlert;
import com.pricecomparatormarket.model.PriceAlertTrigger;

public final class AlertMapper {
  private AlertMapper() {}

  public static AlertDto toAlertDto(PriceAlert a) {
    return new AlertDto(
        a.getId(),
        a.getUserEmail(),
        a.getProduct().getProductId(),
        a.getProduct().getProductName(),
        a.getStore() == null ? null : a.getStore().getId(),
        a.getStore() == null ? null : a.getStore().getName(),
        a.getTargetPrice(),
        a.isActive());
  }

  public static AlertTriggerDto toTriggerDto(PriceAlertTrigger t) {
    return new AlertTriggerDto(
        t.getId(), t.getAlert().getId(), t.getSnapshotDate(), t.getHitPrice(), t.getStoreName());
  }
}
