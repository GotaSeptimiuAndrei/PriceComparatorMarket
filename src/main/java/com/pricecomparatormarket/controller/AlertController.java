package com.pricecomparatormarket.controller;

import com.pricecomparatormarket.dto.request.CreateAlertRequest;
import com.pricecomparatormarket.dto.response.AlertDto;
import com.pricecomparatormarket.dto.response.AlertTriggerDto;
import com.pricecomparatormarket.service.AlertService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

  private final AlertService service;

  @PostMapping
  public AlertDto create(@RequestBody @Valid CreateAlertRequest req) {
    return service.create(req);
  }

  @DeleteMapping("/{id}")
  public void deactivate(@PathVariable Long id) {
    service.deactivate(id);
  }

  @GetMapping
  public List<AlertDto> list(@RequestParam @NotBlank String userEmail) {
    return service.listForUser(userEmail);
  }

  @GetMapping("/{id}/triggers")
  public List<AlertTriggerDto> history(@PathVariable Long id) {
    return service.triggersForAlert(id);
  }
}
