package com.jewelry.jewelryshopbackend.dto.response.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {
    private Long id;
    private String orderCode;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String note;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
