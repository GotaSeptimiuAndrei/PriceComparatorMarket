package com.pricecomparatormarket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "price_alert")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userEmail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY) // nullable → “any store”
  @JoinColumn(name = "store_id")
  private Store store;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal targetPrice;

  @Column(length = 3, nullable = false)
  private String currency = "RON";

  @CreationTimestamp private Instant createdAt;

  private boolean active = true;
}
