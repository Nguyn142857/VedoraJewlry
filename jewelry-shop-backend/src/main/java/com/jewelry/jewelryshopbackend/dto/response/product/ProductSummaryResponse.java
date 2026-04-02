package com.jewelry.jewelryshopbackend.dto.response.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSummaryResponse {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal basePrice;
    private String thumbnail;
    private Boolean status;
    private Long categoryId;
    private String categoryName;
}
