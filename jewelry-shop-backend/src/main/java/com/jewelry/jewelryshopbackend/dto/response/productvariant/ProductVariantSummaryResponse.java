package com.jewelry.jewelryshopbackend.dto.response.productvariant;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVariantSummaryResponse {
    private Long id;
    private String sku;
    private String size;
    private String color;
    private String gemstone;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean status;
}
