package com.jewelry.jewelryshopbackend.dto.request.order;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {

    @Pattern(
            regexp = "^(?i)(PENDING|CONFIRMED|SHIPPING|DELIVERED|CANCELLED)$",
            message = "Invalid order status"
    )
    private String orderStatus;

    @Pattern(
            regexp = "^(?i)(UNPAID|PAID)$",
            message = "Invalid payment status"
    )
    private String paymentStatus;
}
