package com.jewelry.jewelryshopbackend.service.order;

import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;

public interface OrderQueryService {

    OrderResponse getMyOrderById(Long orderId);

    PageResponse<OrderSummaryResponse> getMyOrders(int page, int size, String sortBy, String sortDir);

    OrderResponse getByIdForAdmin(Long orderId);

    PageResponse<OrderSummaryResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long userId,
            String orderStatus,
            String paymentStatus
    );
}
