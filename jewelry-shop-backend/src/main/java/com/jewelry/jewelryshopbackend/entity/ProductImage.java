package com.jewelry.jewelryshopbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isMain = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
