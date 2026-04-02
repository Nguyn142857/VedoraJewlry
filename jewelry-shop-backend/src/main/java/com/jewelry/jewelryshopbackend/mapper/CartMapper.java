package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.cart.CartItemResponse;
import com.jewelry.jewelryshopbackend.dto.response.cart.CartResponse;
import com.jewelry.jewelryshopbackend.entity.Cart;
import com.jewelry.jewelryshopbackend.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productVariantId(item.getProductVariant().getId())
                .productId(item.getProductVariant().getProduct().getId())
                .productName(item.getProductVariant().getProduct().getName())
                .productSlug(item.getProductVariant().getProduct().getSlug())
                .sku(item.getProductVariant().getSku())
                .size(item.getProductVariant().getSize())
                .color(item.getProductVariant().getColor())
                .gemstone(item.getProductVariant().getGemstone())
                .quantity(item.getQuantity())
                .availableStock(item.getProductVariant().getStockQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }

    public CartResponse toCartResponse(Cart cart, List<CartItem> items) {
        List<CartItemResponse> content = items.stream().map(this::toItemResponse).toList();
        BigDecimal total = content.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(content)
                .totalAmount(total)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
