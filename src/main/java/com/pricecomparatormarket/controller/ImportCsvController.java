package com.pricecomparatormarket.controller;

import com.pricecomparatormarket.service.ImportCsvService;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportCsvController {
  private final ImportCsvService service;

  @PostMapping
  public ResponseEntity<String> triggerImport(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
      throws IOException {
    service.importForDate(date);
    return ResponseEntity.ok("Imported CSVs for date: " + date);
  }
}
