package com.pricecomparatormarket.service;

import com.pricecomparatormarket.model.PriceAlert;
import com.pricecomparatormarket.model.PriceAlertTrigger;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.repository.PriceAlertRepository;
import com.pricecomparatormarket.repository.PriceAlertTriggerRepository;
import com.pricecomparatormarket.util.notification.NotificationPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertEvaluator {

  private final PriceAlertRepository alerts;
  private final PriceAlertTriggerRepository triggers;
  private final NotificationPort notifier;

  @Transactional
  public void evaluate(PriceSnapshot ps) {

    var matches =
        alerts.findMatching(ps.getProduct().getProductId(), ps.getStore().getId(), ps.getPrice());

    for (PriceAlert a : matches) {
      PriceAlertTrigger trg =
          triggers.save(
              new PriceAlertTrigger(
                  null, a, ps.getId().getSnapshotDate(), ps.getPrice(), ps.getStore().getName()));

      notifier.notifyHit(a.getUserEmail(), trg);

      a.setActive(false);
    }
  }
}
