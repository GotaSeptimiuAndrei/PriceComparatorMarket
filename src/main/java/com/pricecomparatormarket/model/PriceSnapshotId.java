package com.pricecomparatormarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceSnapshotId implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "store_id")
  private Long storeId;

  @Column(name = "product_id", length = 20)
  private String productId;

  @Column(name = "snapshot_date")
  private LocalDate snapshotDate;
}
