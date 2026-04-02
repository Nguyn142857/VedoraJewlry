package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageSummaryResponse;
import com.jewelry.jewelryshopbackend.entity.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper {

    public ProductImageResponse toResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .productName(image.getProduct().getName())
                .productSlug(image.getProduct().getSlug())
                .imageUrl(image.getImageUrl())
                .isMain(image.getIsMain())
                .deleted(image.getDeleted())
                .sortOrder(image.getSortOrder())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }

    public ProductImageSummaryResponse toSummary(ProductImage image) {
        ProductImageSummaryResponse response = new ProductImageSummaryResponse();
        response.setId(image.getId());
        response.setImageUrl(image.getImageUrl());
        response.setIsMain(image.getIsMain());
        response.setSortOrder(image.getSortOrder());
        return response;
    }
}
