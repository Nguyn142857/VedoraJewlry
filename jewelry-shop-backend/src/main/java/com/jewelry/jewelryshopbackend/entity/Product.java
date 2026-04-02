package com.jewelry.jewelryshopbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseEntity {


    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String material;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Column(length = 255)
    private String thumbnail;

    @Default
    @Column(nullable = false)
    private Boolean status = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}