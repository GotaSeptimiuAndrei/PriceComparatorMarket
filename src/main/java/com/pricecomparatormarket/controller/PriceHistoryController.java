package com.pricecomparatormarket.controller;

import com.pricecomparatormarket.dto.response.PriceHistoryDto;
import com.pricecomparatormarket.service.PriceHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/price-history")
@RequiredArgsConstructor
public class PriceHistoryController {

  private final PriceHistoryService priceHistoryService;

  @GetMapping
  public List<PriceHistoryDto> history(
      @RequestParam(required = false) String productId,
      @RequestParam(required = false) Long storeId,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand) {

    return priceHistoryService.getHistory(productId, storeId, category, brand);
  }
}
