package com.jewelry.jewelryshopbackend.service.order;

import com.jewelry.jewelryshopbackend.dto.request.order.OrderCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.order.OrderStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;

public interface OrderCommandService {

    OrderResponse createFromCart(OrderCreateRequest request);

    OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request);
}
