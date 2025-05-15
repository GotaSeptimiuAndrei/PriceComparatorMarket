package com.pricecomparatormarket.service;

import com.pricecomparatormarket.dto.request.CreateAlertRequest;
import com.pricecomparatormarket.dto.response.AlertDto;
import com.pricecomparatormarket.dto.response.AlertTriggerDto;
import com.pricecomparatormarket.exception.NotFoundException;
import com.pricecomparatormarket.mapper.AlertMapper;
import com.pricecomparatormarket.model.PriceAlert;
import com.pricecomparatormarket.model.Product;
import com.pricecomparatormarket.model.Store;
import com.pricecomparatormarket.repository.PriceAlertRepository;
import com.pricecomparatormarket.repository.PriceAlertTriggerRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

  private final PriceAlertRepository alerts;
  private final PriceAlertTriggerRepository triggers;
  private final EntityManager em;

  public AlertDto create(CreateAlertRequest req) {

    Product p = em.getReference(Product.class, req.productId());

    Store s = (req.storeId() == null) ? null : em.getReference(Store.class, req.storeId());

    PriceAlert saved =
        alerts.save(
            new PriceAlert(null, req.userEmail(), p, s, req.targetPrice(), "RON", null, true));

    return AlertMapper.toAlertDto(saved);
  }

  public void deactivate(Long id) {
    PriceAlert a =
        alerts.findById(id).orElseThrow(() -> new NotFoundException("Alert " + id + " not found"));
    a.setActive(false);
    alerts.save(a);
  }

  public List<AlertDto> listForUser(String email) {
    return alerts.findByUserEmail(email).stream().map(AlertMapper::toAlertDto).toList();
  }

  public List<AlertTriggerDto> triggersForAlert(Long id) {
    PriceAlert a =
        alerts.findById(id).orElseThrow(() -> new NotFoundException("Alert " + id + " not found"));
    return triggers.findByAlert(a).stream().map(AlertMapper::toTriggerDto).toList();
  }
}
