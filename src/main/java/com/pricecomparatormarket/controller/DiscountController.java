package com.pricecomparatormarket.controller;

import com.pricecomparatormarket.dto.response.BestDiscountDto;
import com.pricecomparatormarket.service.DiscountService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

  private final DiscountService service;

  @GetMapping("/best")
  public ResponseEntity<List<BestDiscountDto>> bestDiscounts(
      @RequestParam(defaultValue = "20") @Min(1) @Max(20) int limit,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate date) {
    return ResponseEntity.ok(service.getBestDiscounts(limit, date));
  }
}
