package com.pricecomparatormarket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "price_alert_trigger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlertTrigger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "alert_id", nullable = false)
  private PriceAlert alert;

  private LocalDate snapshotDate;
  private BigDecimal hitPrice;
  private String storeName;
}
