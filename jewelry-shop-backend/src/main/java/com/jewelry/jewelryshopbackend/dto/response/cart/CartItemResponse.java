package com.jewelry.jewelryshopbackend.dto.response.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemResponse {
    private Long id;
    private Long productVariantId;
    private Long productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String size;
    private String color;
    private String gemstone;
    private Integer quantity;
    private Integer availableStock;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
