package com.pricecomparatormarket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "discount")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "from_date", nullable = false)
  private LocalDate fromDate;

  @Column(name = "to_date", nullable = false)
  private LocalDate toDate;

  @Column(name = "percentage_of_discount", nullable = false, precision = 5, scale = 2)
  private BigDecimal percentageOfDiscount;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;
}
