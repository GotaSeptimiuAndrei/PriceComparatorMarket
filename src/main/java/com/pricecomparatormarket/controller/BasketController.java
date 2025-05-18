package com.pricecomparatormarket.controller;

import com.pricecomparatormarket.dto.request.BasketOptimizeRequest;
import com.pricecomparatormarket.dto.response.SuggestedListDto;
import com.pricecomparatormarket.service.BasketService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
public class BasketController {

  private final BasketService svc;

  @PostMapping("/optimise")
  public List<SuggestedListDto> optimise(@RequestBody @Valid BasketOptimizeRequest req) {
    return svc.optimiseAndNotify(req);
  }
}
