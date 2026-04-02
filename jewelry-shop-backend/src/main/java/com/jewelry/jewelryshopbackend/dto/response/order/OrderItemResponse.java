package com.jewelry.jewelryshopbackend.dto.response.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productVariantId;
    private String productName;
    private String variantInfo;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
