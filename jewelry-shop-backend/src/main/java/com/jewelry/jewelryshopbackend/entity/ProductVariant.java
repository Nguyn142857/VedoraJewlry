package com.jewelry.jewelryshopbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(length = 50)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(length = 100)
    private String gemstone;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(nullable = false)
    private Boolean status = true;
}