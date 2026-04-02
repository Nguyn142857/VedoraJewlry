package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.order.OrderItemResponse;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderResponse;
import com.jewelry.jewelryshopbackend.dto.response.order.OrderSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productVariantId(item.getProductVariant().getId())
                .productName(item.getProductName())
                .variantInfo(item.getVariantInfo())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public OrderResponse toOrderResponse(Order order, List<OrderItem> items) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .paymentMethod(order.getPaymentMethod().name())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getOrderStatus().name())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .note(order.getNote())
                .items(items.stream().map(this::toItemResponse).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderSummaryResponse toSummaryResponse(Order order) {
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .finalAmount(order.getFinalAmount())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getOrderStatus().name())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
