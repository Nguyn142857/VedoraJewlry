package com.jewelry.jewelryshopbackend.dto.response.productvariant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductVariantResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String size;
    private String color;
    private String gemstone;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean status;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
