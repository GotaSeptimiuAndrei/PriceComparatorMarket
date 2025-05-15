package com.pricecomparatormarket.util.notification;

import com.pricecomparatormarket.model.PriceAlertTrigger;

public interface NotificationPort {
  void notifyHit(String toEmail, PriceAlertTrigger trigger);
}
