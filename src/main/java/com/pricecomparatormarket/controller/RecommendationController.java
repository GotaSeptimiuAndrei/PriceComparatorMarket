package com.pricecomparatormarket.controller;

import com.pricecomparatormarket.dto.response.RecommendationDto;
import com.pricecomparatormarket.service.RecommendationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService service;

  @GetMapping("/best-buy")
  public List<RecommendationDto> bestBuys(
      @RequestParam String category,
      @RequestParam(defaultValue = "20") @Min(1) @Max(20) int limit) {

    return service.bestBuys(category, limit);
  }
}
