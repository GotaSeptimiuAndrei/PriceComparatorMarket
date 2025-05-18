package com.pricecomparatormarket.service;

import com.pricecomparatormarket.model.Discount;
import com.pricecomparatormarket.model.PriceAlert;
import com.pricecomparatormarket.model.PriceAlertTrigger;
import com.pricecomparatormarket.model.PriceSnapshot;
import com.pricecomparatormarket.repository.PriceAlertRepository;
import com.pricecomparatormarket.repository.PriceAlertTriggerRepository;
import com.pricecomparatormarket.repository.PriceSnapshotRepository;
import com.pricecomparatormarket.util.notification.NotificationPort;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertEvaluator {

  private final PriceAlertRepository alerts;
  private final PriceAlertTriggerRepository triggers;
  private final NotificationPort notifier;
  private final PriceSnapshotRepository snapshots;

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

  @Transactional
  public void evaluate(Discount d, LocalDate importDate) {

    // run only if the imported file’s date lies INSIDE the discount interval
    if (importDate.isBefore(d.getFromDate()) || importDate.isAfter(d.getToDate())) {
      return;
    }

    snapshots
        .findOneByStoreProductAndDate(
            d.getStore().getId(), d.getProduct().getProductId(), importDate)
        .ifPresent(
            ps -> {
              BigDecimal discounted =
                  ps.getPrice()
                      .multiply(
                          BigDecimal.ONE.subtract(
                              d.getPercentageOfDiscount().divide(BigDecimal.valueOf(100))));

              var matches =
                  alerts.findMatching(
                      ps.getProduct().getProductId(), ps.getStore().getId(), discounted);

              for (PriceAlert a : matches) {
                PriceAlertTrigger trg =
                    triggers.save(
                        new PriceAlertTrigger(
                            null, a, importDate, discounted, ps.getStore().getName()));
                notifier.notifyHit(a.getUserEmail(), trg);
                a.setActive(false);
              }
            });
  }
}
