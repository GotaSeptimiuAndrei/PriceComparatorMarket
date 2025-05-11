package com.pricecomparatormarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "price_snapshot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceSnapshot {

    @EmbeddedId
    private PriceSnapshotId id;

    @MapsId("storeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "package_quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal packageQuantity;

    @Column(name = "package_unit", length = 10, nullable = false)
    private String packageUnit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 3, nullable = false)
    private String currency;
}
