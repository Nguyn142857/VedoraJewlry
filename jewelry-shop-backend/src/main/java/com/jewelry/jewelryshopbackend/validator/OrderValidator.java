package com.jewelry.jewelryshopbackend.validator;

import com.jewelry.jewelryshopbackend.entity.Order;
import com.jewelry.jewelryshopbackend.enums.OrderStatus;
import com.jewelry.jewelryshopbackend.enums.PaymentStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderValidator {

    public void validateAmount(BigDecimal shippingFee, BigDecimal discountAmount, BigDecimal totalAmount) {
        BigDecimal safeShipping = shippingFee == null ? BigDecimal.ZERO : shippingFee;
        BigDecimal safeDiscount = discountAmount == null ? BigDecimal.ZERO : discountAmount;

        if (safeShipping.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Shipping fee must be greater than or equal to 0");
        }
        if (safeDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Discount amount must be greater than or equal to 0");
        }
        if (safeDiscount.compareTo(totalAmount) > 0) {
            throw new BadRequestException("Discount amount must not exceed total amount");
        }
    }

    public OrderStatus parseOrderStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid order status");
        }
    }

    public PaymentStatus parsePaymentStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid payment status");
        }
    }

    public void validateStatusTransition(Order order, OrderStatus newOrderStatus) {
        if (newOrderStatus == null) {
            return;
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Finalized order cannot change status");
        }
    }
}
