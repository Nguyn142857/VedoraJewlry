package com.jewelry.jewelryshopbackend.dto.response.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
