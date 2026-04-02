package com.jewelry.jewelryshopbackend.dto.response.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderSummaryResponse {
    private Long id;
    private String orderCode;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String orderStatus;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime createdAt;
}
