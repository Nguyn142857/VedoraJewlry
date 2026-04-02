package com.jewelry.jewelryshopbackend.dto.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {

    private String orderStatus;

    private String paymentStatus;
}
