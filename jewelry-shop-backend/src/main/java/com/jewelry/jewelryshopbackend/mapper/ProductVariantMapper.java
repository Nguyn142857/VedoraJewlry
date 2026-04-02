package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.ProductVariant;
import org.springframework.stereotype.Component;

@Component
public class ProductVariantMapper {

    public ProductVariantResponse toResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .productSlug(variant.getProduct().getSlug())
                .sku(variant.getSku())
                .size(variant.getSize())
                .color(variant.getColor())
                .gemstone(variant.getGemstone())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .status(variant.getStatus())
                .deleted(variant.getDeleted())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }

    public ProductVariantSummaryResponse toSummary(ProductVariant variant) {
        ProductVariantSummaryResponse response = new ProductVariantSummaryResponse();
        response.setId(variant.getId());
        response.setSku(variant.getSku());
        response.setSize(variant.getSize());
        response.setColor(variant.getColor());
        response.setGemstone(variant.getGemstone());
        response.setPrice(variant.getPrice());
        response.setStockQuantity(variant.getStockQuantity());
        response.setStatus(variant.getStatus());
        return response;
    }
}
