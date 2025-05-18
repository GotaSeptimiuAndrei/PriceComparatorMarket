package com.pricecomparatormarket.service;

import com.pricecomparatormarket.dto.request.BasketOptimizeRequest;
import com.pricecomparatormarket.dto.response.SuggestedListDto;
import com.pricecomparatormarket.util.BasketOptimizer;
import com.pricecomparatormarket.util.notification.BasketEmailBuilder;
import com.pricecomparatormarket.util.notification.NotificationPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketService {

  private final BasketOptimizer optimizer;
  private final BasketEmailBuilder renderer;
  private final NotificationPort notifier;

  public List<SuggestedListDto> optimiseAndNotify(BasketOptimizeRequest req) {

    LocalDate today = LocalDate.now();

    var result = optimizer.optimise(req.items(), req.maxStores(), today);

    BigDecimal grand =
        result.stream().map(SuggestedListDto::subTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

    String mailBody = renderer.buildEmail(req.name(), result, grand);
    notifier.sendText(req.userEmail(), mailBody);

    return result;
  }
}
